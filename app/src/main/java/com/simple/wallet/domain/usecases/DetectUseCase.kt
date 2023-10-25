package com.simple.wallet.domain.usecases

import android.graphics.PointF
import android.graphics.Rect
import android.util.Size
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.simple.core.utils.extentions.resumeActive
import com.simple.coreapp.data.usecase.BaseUseCase
import com.simple.state.ResultState
import com.simple.state.toSuccess
import com.simple.task.executeAsyncAll
import com.simple.task.executeAsyncByFast
import com.simple.wallet.domain.entities.scan.ScanData
import com.simple.wallet.domain.entities.scan.ScanInputType
import com.simple.wallet.domain.entities.scan.ScanOutputType
import com.simple.wallet.domain.tasks.CameraDetectTask
import com.simple.wallet.domain.tasks.OutputTypeTask
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.math.max
import kotlin.math.min


internal class DetectUseCase(
    private val detectTaskList: List<CameraDetectTask>,
    private val outputTypeTaskList: List<OutputTypeTask>
) : BaseUseCase<DetectUseCase.Param, List<ScanData>> {

    private val qrcodeScanner: BarcodeScanner = BarcodeScanning.getClient(
        BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .build()
    )

    private val textRecognizer: TextRecognizer = TextRecognition.getClient(
        TextRecognizerOptions.Builder()
            .build()
    )

    override suspend fun execute(param: Param?): List<ScanData> {
        checkNotNull(param)

        val image = InputImage.fromMediaImage(param.imageProxy.image!!, param.imageProxy.imageInfo.rotationDegrees)


        val imageProxyWidth: Int
        val imageProxyHeight: Int

        if (param.size.height > param.size.width) {

            imageProxyWidth = min(param.imageProxy.height, param.imageProxy.width)
            imageProxyHeight = max(param.imageProxy.height, param.imageProxy.width)
        } else {

            imageProxyWidth = max(param.imageProxy.height, param.imageProxy.width)
            imageProxyHeight = min(param.imageProxy.height, param.imageProxy.width)
        }


        val ratio = min(imageProxyHeight * 1f / param.size.height, imageProxyWidth * 1f / param.size.width)


        val sizeRadio = param.size.let { Size((it.width * ratio).toInt(), (it.height * ratio).toInt()) }

        val rectCropRadio = param.points.wrap(ratio).toRect()

        val deltaMoveY = (sizeRadio.height - imageProxyHeight) / 2
        val deltaMoveX = (sizeRadio.width - imageProxyWidth) / 2


        val rectCropMove = rectCropRadio.let { Rect(it.left - deltaMoveX, it.top - deltaMoveY, it.right - deltaMoveX, it.bottom - deltaMoveY) }


        val dataList = if (param.scanInputType == ScanInputType.Qrcode) {

            handlerQrcodeScanner(image, rectCropMove)
        } else {

            handlerTextRecognizer(image, rectCropMove)
        }


        val outputTypeList = outputTypeTaskList.executeAsyncByFast(param.action).toSuccess()?.data ?: return dataList

        return detectTaskList.executeAsyncAll(CameraDetectTask.Param(outputTypeList, dataList)).first().toSuccess()?.data?.filterIsInstance<ResultState.Success<List<ScanData>>>()?.flatMap { it.data } ?: return dataList
    }


    private suspend fun handlerQrcodeScanner(image: InputImage, rect: Rect) = suspendCancellableCoroutine<List<ScanData>> { emitter ->

        val task = qrcodeScanner.process(image)

        emitter.invokeOnCancellation {
        }

        task.addOnSuccessListener { text ->

            val scanData = text.filter {

                rect.contains(it.boundingBox ?: Rect())
            }.mapNotNull {

                it.rawValue
            }.map {

                ScanData(it, ScanOutputType.Qrcode, ScanInputType.Qrcode)
            }

            emitter.resumeActive(scanData)
        }.addOnFailureListener {

            emitter.resumeActive(emptyList())
        }
    }

    private suspend fun handlerTextRecognizer(image: InputImage, rect: Rect) = suspendCancellableCoroutine<List<ScanData>> { emitter ->

        val task = textRecognizer.process(image)

        emitter.invokeOnCancellation {
        }

        task.addOnSuccessListener { text ->

            val scanData = text.textBlocks.map {

                it.lines.filter { line -> rect.contains(line.boundingBox ?: Rect()) }.joinToString("") { line -> line.text }
            }.filter {

                it.isNotBlank()
            }.map {

                ScanData(it, ScanOutputType.Qrcode, ScanInputType.Text)
            }

            emitter.resumeActive(scanData)
        }.addOnFailureListener {

            emitter.resumeActive(emptyList())
        }
    }

    private fun List<PointF>.toRect() = Rect(
        this[0].x.toInt(),
        this[0].y.toInt(),
        this[2].x.toInt(),
        this[2].y.toInt()
    )

    private fun List<PointF>.wrap(ratio: Float) = map {

        PointF(it.x * ratio, it.y * ratio)
    }

    data class Param(val imageProxy: ImageProxy, val size: Size, val points: List<PointF>, val scanInputType: ScanInputType, val action: String)
}
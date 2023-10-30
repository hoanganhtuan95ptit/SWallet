package com.simple.wallet.presentation.camera

import android.graphics.PointF
import android.util.Size
import androidx.camera.core.ImageProxy
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.viewModelScope
import com.simple.adapter.ViewItemCloneable
import com.simple.coreapp.ui.base.viewmodels.BaseViewModel
import com.simple.coreapp.utils.extentions.combineSources
import com.simple.coreapp.utils.extentions.getOrDefault
import com.simple.coreapp.utils.extentions.getOrEmpty
import com.simple.coreapp.utils.extentions.liveData
import com.simple.coreapp.utils.extentions.postDifferentValue
import com.simple.state.toSuccess
import com.simple.task.executeAsyncByFast
import com.simple.wallet.domain.entities.scan.ScanData
import com.simple.wallet.domain.entities.scan.ScanInputType
import com.simple.wallet.domain.tasks.CameraActionTask
import com.simple.wallet.domain.tasks.CameraInfoTask
import com.simple.wallet.domain.usecases.camera.CameraDetectUseCase
import com.simple.wallet.domain.usecases.camera.GetInputUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

internal class CameraViewModel(
    private val action: String,

    private val cameraDetectUseCase: CameraDetectUseCase,
    private val getInputUseCase: GetInputUseCase,

    private val cameraInfoTaskList: List<CameraInfoTask>,
    private val cameraActionTaskList: List<CameraActionTask>
) : BaseViewModel() {


    val tabIndex: LiveData<Int> = MediatorLiveData()

    val inputTypeList: LiveData<List<ScanInputType>> = liveData {

        getInputUseCase.execute(param = action).let {

            postValue(it)
        }
    }


    val infoViewItemList: LiveData<List<ViewItemCloneable>> = liveData {

        val list = cameraInfoTaskList.executeAsyncByFast(action).toSuccess()?.data ?: emptyList()

        postValue(list)
    }

    val actionViewItemList: LiveData<List<ViewItemCloneable>> = combineSources(tabIndex, inputTypeList) {

        val inputType = inputTypeList.getOrEmpty()[tabIndex.getOrDefault(0)]

        val list = cameraActionTaskList.executeAsyncByFast(CameraActionTask.Param(action, inputType)).toSuccess()?.data ?: emptyList()

        postValue(list)
    }


    val processState: LiveData<List<ScanData>> = MediatorLiveData()


    fun process(imageProxy: ImageProxy, size: Size, points: List<PointF>) = viewModelScope.launch(handler + Dispatchers.IO) {

        val inputType = inputTypeList.getOrEmpty()[tabIndex.getOrDefault(0)]

        imageProxy.use { imageProxy ->

            processState.postDifferentValue(cameraDetectUseCase.execute(CameraDetectUseCase.Param(imageProxy, size, points, inputType, action)))
        }
    }

    fun updateTabIndex(tabIndex: Int) {

        this.tabIndex.postDifferentValue(tabIndex)
    }
}
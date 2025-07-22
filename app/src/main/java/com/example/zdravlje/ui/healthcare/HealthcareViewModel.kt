package com.example.zdravlje.ui.healthcare

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zdravlje.data.model.healthcare.MedicineCompareItem
import com.example.zdravlje.data.model.request.PinRequest
import com.example.zdravlje.data.model.request.calendar.ExamDeleteRequest
import com.example.zdravlje.data.model.request.healthcare.AddMedicamentRequest
import com.example.zdravlje.data.model.request.healthcare.CommonsThemesRequest
import com.example.zdravlje.data.model.request.healthcare.DeleteMedicamenteRequest
import com.example.zdravlje.data.model.request.healthcare.ESickRequest
import com.example.zdravlje.data.model.request.healthcare.ESickRequestDTO
import com.example.zdravlje.data.model.request.healthcare.EventHistoryDataRequest
import com.example.zdravlje.data.model.request.healthcare.FeedbackRequest
import com.example.zdravlje.data.model.request.healthcare.MKB10Response
import com.example.zdravlje.data.model.request.healthcare.MKB10SubResponse
import com.example.zdravlje.data.model.request.healthcare.MKB10SubResponseGroups
import com.example.zdravlje.data.model.request.healthcare.MedicalCartonPdfRequest
import com.example.zdravlje.data.model.request.healthcare.MedicalCartonRequest
import com.example.zdravlje.data.model.request.healthcare.PatmedDataRequest
import com.example.zdravlje.data.model.request.healthcare.PreventionDTO
import com.example.zdravlje.data.model.request.healthcare.RecipeRequest
import com.example.zdravlje.data.model.request.healthcare.StaticRagRequest
import com.example.zdravlje.data.model.response.healthcare.AccessMedicalCartonResponse
import com.example.zdravlje.data.model.response.healthcare.AppointmentResponse
import com.example.zdravlje.data.model.response.healthcare.CategoryQAResponse
import com.example.zdravlje.data.model.response.healthcare.EventHistoryDataResponse
import com.example.zdravlje.data.model.response.healthcare.Institution
import com.example.zdravlje.data.model.response.healthcare.InstitutionsArrayResponse
import com.example.zdravlje.data.model.response.healthcare.InstitutionsResponse
import com.example.zdravlje.data.model.response.healthcare.InteractionMedicineResponse
import com.example.zdravlje.data.model.response.healthcare.MedicalCartonResponse
import com.example.zdravlje.data.model.response.healthcare.MedicamenteResponse
import com.example.zdravlje.data.model.response.healthcare.PrescribedListMedicamentResponse
import com.example.zdravlje.data.model.response.healthcare.QAResponse
import com.example.zdravlje.data.model.response.healthcare.RecommendationResponse
import com.example.zdravlje.data.model.response.healthcare.SickLeaveResponse
import com.example.zdravlje.data.model.response.healthcare.SocialGroupCategoryResponse
import com.example.zdravlje.data.model.response.healthcare.StaticRagResponse
import com.example.zdravlje.data.model.response.healthcare.TopicQAResponse
import com.example.zdravlje.data.repository.DataStoreRepository
import com.example.zdravlje.data.repository.healthcare.HealthcareRepository
import com.example.zdravlje.services.avc.SampleApplication.Companion.context
import com.example.zdravlje.ui.base.SingleLiveEvent
import com.google.gson.JsonObject
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Response
import com.nites.ezdravljeapp.R
import org.json.JSONObject

class HealthcareViewModel(
    private val healthcareRepository: HealthcareRepository,
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {

    var healthInstitutionName: String? = null
    var healthCategoryName: String? = null
    var healthTopicName: String? = null
    var isPinEntered: Boolean = false

    var dateOllList: MutableLiveData<List<String>> = MutableLiveData()
    var statusOll: MutableLiveData<Int> = MutableLiveData()
    var typeOll: MutableLiveData<Int> = MutableLiveData()

    var dateECartonList: MutableLiveData<List<String>> = MutableLiveData()
    var institutionCode: MutableLiveData<String> = MutableLiveData()

    var listNameInn: MutableLiveData<List<MedicineCompareItem>> = MutableLiveData()

    private val _searchPrescriptionsLiveData = SingleLiveEvent<Response<List<PrescribedListMedicamentResponse>>>()
    val searchPrescriptionsLiveData: LiveData<Response<List<PrescribedListMedicamentResponse>>> = _searchPrescriptionsLiveData

    private val _searchPrescriptionsErrorLiveData = SingleLiveEvent<String>()
    val searchPrescriptionsErrorLiveData: LiveData<String> = _searchPrescriptionsErrorLiveData

    private val _deleteMedicamenteManualLiveData = SingleLiveEvent<Response<Any>>()
    val deleteMedicamenteManualLiveData: LiveData<Response<Any>> = _deleteMedicamenteManualLiveData


    private val _deleteMedicamenteManualErrorLiveData = SingleLiveEvent<String>()
    val deleteMedicamenteManualErrorLiveData: LiveData<String> = _deleteMedicamenteManualErrorLiveData

    val updateMedicamenteLiveData: MutableLiveData<Response<Any>> =
        MutableLiveData()
    val updateMedicamenteErrorLiveData: MutableLiveData<String> = MutableLiveData()

    val getPatientSickLeavesLiveData: MutableLiveData<Response<List<SickLeaveResponse>>> =
        MutableLiveData()
    val getPatientSickLeavesErrorLiveData: MutableLiveData<String> = MutableLiveData()

    private val _inabilityReportLiveData = SingleLiveEvent<Response<ResponseBody>>()
    val inabilityReportLiveData: LiveData<Response<ResponseBody>> = _inabilityReportLiveData

    val inabilityReportErrorLiveData: MutableLiveData<String> = MutableLiveData()

    private val _inabilityReceiptLiveData = SingleLiveEvent<Response<ResponseBody>>()
    val inabilityReceiptLiveData: LiveData<Response<ResponseBody>> = _inabilityReceiptLiveData

    val inabilityReceiptErrorLiveData: MutableLiveData<String> = MutableLiveData()

    val getSickReportErrorLiveData: MutableLiveData<String> = MutableLiveData()

    private val _getSickReportLiveData = SingleLiveEvent<Response<ResponseBody>>()
    val getSickReportLiveData: LiveData<Response<ResponseBody>> = _getSickReportLiveData

    val getMedicalCardLiveData: MutableLiveData<Response<List<MedicalCartonResponse>>?> =
        MutableLiveData()
    val getMedicalCardErrorLiveData: MutableLiveData<String> = MutableLiveData()

    val reportMedicalCardLiveData: MutableLiveData<Response<ResponseBody>> = MutableLiveData()
    val reportMedicalCardErrorLiveData: MutableLiveData<String> = MutableLiveData()

    val getInstitutionsLiveData: MutableLiveData<Response<InstitutionsResponse>> = MutableLiveData()
    val getInstitutionsErrorLiveData: MutableLiveData<String> = MutableLiveData()

    val getInstitutionsArrLiveData: MutableLiveData<Response<InstitutionsArrayResponse>> =
        MutableLiveData()
    val getInstitutionsArrErrorLiveData: MutableLiveData<String> = MutableLiveData()

    val getRefreshInstitutionDataLiveData: MutableLiveData<Response<String>> =
        MutableLiveData()
    val getRefreshInstitutionDataErrorLiveData: MutableLiveData<String> = MutableLiveData()

    val getAccessMedicalCartonLiveData: MutableLiveData<Response<List<AccessMedicalCartonResponse>>> =
        MutableLiveData()
    val getAccessMedicalCartonErrorLiveData: MutableLiveData<String> = MutableLiveData()

    val getCommonsThemesLiveData: MutableLiveData<Response<List<SocialGroupCategoryResponse>>> =
        MutableLiveData()
    val getCommonsThemesErrorLiveData: MutableLiveData<String> = MutableLiveData()

    val getLikesLiveData: MutableLiveData<Response<List<SocialGroupCategoryResponse>>> =
        MutableLiveData()
    val getLikesErrorLiveData: MutableLiveData<String> = MutableLiveData()

    val followSocialGroupLiveData: MutableLiveData<Response<String>> = MutableLiveData()
    val followSocialGroupErrorLiveData: MutableLiveData<String> = MutableLiveData()

    val getHealthInstitutionLiveData: MutableLiveData<Response<List<Institution>>> =
        MutableLiveData()
    val getHealthInstitutionErrorLiveData: MutableLiveData<String> = MutableLiveData()

    val getCategoryQALiveData: MutableLiveData<Response<List<CategoryQAResponse>>> =
        MutableLiveData()
    val getCategoryQAErrorLiveData: MutableLiveData<String> = MutableLiveData()

    val getTopicQALiveData: MutableLiveData<Response<List<TopicQAResponse>>> = MutableLiveData()
    val getTopicQAErrorLiveData: MutableLiveData<String> = MutableLiveData()

    val getAllQALiveData: MutableLiveData<Response<List<QAResponse>>> = MutableLiveData()
    val getAllQAErrorLiveData: MutableLiveData<String> = MutableLiveData()

    val getRecommendationLiveData: MutableLiveData<Response<List<RecommendationResponse>>> =
        MutableLiveData()
    val getRecommendationErrorLiveData: MutableLiveData<String> = MutableLiveData()

    val getMedicamenteLiveData: MutableLiveData<Response<List<MedicamenteResponse>>> =
        MutableLiveData()
    val getMedicamenteErrorLiveData: MutableLiveData<String> = MutableLiveData()

    val getMKB10LiveData: MutableLiveData<Response<List<MKB10Response>>> = MutableLiveData()
    val getMKB10ErrorLiveData: MutableLiveData<String> = MutableLiveData()

    val getMKB10SubLiveData: MutableLiveData<Response<List<MKB10SubResponse>>> = MutableLiveData()
    val getMKB10SubErrorLiveData: MutableLiveData<String> = MutableLiveData()

    val getMKB10SubGroupsLiveData: MutableLiveData<Response<List<MKB10SubResponseGroups>>> =
        MutableLiveData()
    val getMKB10SubGroupsErrorLiveData: MutableLiveData<String> = MutableLiveData()

    val addMedicamentPrescribedLiveData: MutableLiveData<Response<Any>?> =
        MutableLiveData()
    val addMedicamentPrescribedErrorLiveData: MutableLiveData<String?> = MutableLiveData()

    val getFutureExamsLiveData: MutableLiveData<Response<List<AppointmentResponse>>> =
        MutableLiveData()
    val getFutureExamsErrorLiveData: MutableLiveData<String> = MutableLiveData()

    val getPastScheduledLiveData: MutableLiveData<Response<List<EventHistoryDataResponse>>> =
        MutableLiveData()
    val getPastScheduledErrorLiveData: MutableLiveData<String> = MutableLiveData()

    val deleteExamLiveData: MutableLiveData<Response<Any>> = MutableLiveData()
    val deleteExamErrorLiveData: MutableLiveData<String> = MutableLiveData()

    val getIteractionMedicineLiveData: MutableLiveData<Response<List<InteractionMedicineResponse>>> = MutableLiveData()
    val getIteractionMedicineErrorLiveData: MutableLiveData<String> = MutableLiveData()

    val getStaticRagLiveData: MutableLiveData<Response<StaticRagResponse>> = MutableLiveData()
    val getStaticRagErrorLiveData: MutableLiveData<String> = MutableLiveData()

    val getSaveFeedbackLiveData: MutableLiveData<Response<JsonObject>?> = MutableLiveData()
    val getSaveFeedbackErrorLiveData: MutableLiveData<String> = MutableLiveData()

    fun searchPrescriptions(recipeRequest: RecipeRequest) {
        viewModelScope.launch {
            try {
                val response = healthcareRepository.searchPrescriptions(recipeRequest)
                if (response.isSuccessful) {
                    _searchPrescriptionsLiveData.postValue(response)
                } else {
                    _searchPrescriptionsErrorLiveData.postValue("${context?.getString(R.string.error)}: ${response.message()}")
                }
            } catch (e: Exception) {
                _searchPrescriptionsErrorLiveData .postValue("${context?.getString(R.string.exception)}: ${e.message}")
            }
        }
    }

    fun deleteManualMedicamente(deleteMedicamenteRequest: DeleteMedicamenteRequest) {
        viewModelScope.launch {
            try {
                val response =
                    healthcareRepository.deleteManualMedicamente(deleteMedicamenteRequest)
                if (response.isSuccessful) {
                    _deleteMedicamenteManualLiveData.postValue(response)
                } else {
                    _deleteMedicamenteManualErrorLiveData.postValue(response.message())
                }
            } catch (e: Exception) {
                _deleteMedicamenteManualErrorLiveData.postValue("${context?.getString(R.string.exception)}: ${e.message}")
            }
        }
    }

    fun updateMedicamente(patmedDataRequest: PatmedDataRequest) {
        viewModelScope.launch {
            try {
                val response = healthcareRepository.updateMedicamente(patmedDataRequest)
                if (response.isSuccessful) {
                    updateMedicamenteLiveData.value = response
                } else {
                    _deleteMedicamenteManualErrorLiveData.postValue(response.message())
                }
            } catch (e: Exception) {
                updateMedicamenteErrorLiveData.postValue("${context?.getString(R.string.exception)}: ${e.message}")
            }
        }
    }

    fun addMedicamentPrescribed(addMedicamentRequest: AddMedicamentRequest) {
        addMedicamentPrescribedLiveData.value = null
        viewModelScope.launch {
            try {
                val response = healthcareRepository.addMedicamentPrescribed(addMedicamentRequest)
                if (response.isSuccessful) {
                    addMedicamentPrescribedLiveData.value = response
                } else {
                    val errorText = try {
                        val errorJson = response.errorBody()?.string()
                        val jsonObject = JSONObject(errorJson ?: "{}")
                        jsonObject.optString("message", "${R.string.error}")
                    } catch (e: Exception) {
                        "${R.string.error}: ${e.localizedMessage}"
                    }
                    addMedicamentPrescribedErrorLiveData.value = "${context?.getString(R.string.error)}: $errorText"
                }
            } catch (e: Exception) {
                addMedicamentPrescribedErrorLiveData.value = ("${context?.getString(R.string.exception)}: ${e.message}")
            }
        }
    }

    fun getPatientSickLeaves(eSickRequest: ESickRequest) {
        viewModelScope.launch {
            try {
                val response = healthcareRepository.getPatientSickLeaves(eSickRequest)
                if (response.isSuccessful) {
                    getPatientSickLeavesLiveData.value = response
                } else {
                    getPatientSickLeavesErrorLiveData.postValue("${context?.getString(R.string.error)}: ${response.message()}")
                }
            } catch (e: Exception) {
                getPatientSickLeavesErrorLiveData.postValue("${context?.getString(R.string.exception)}: ${e.message}")
            }
        }
    }

    fun getInabilityReceipt(eSickRequestDTO: ESickRequestDTO) {
        viewModelScope.launch {
            try {
                val response = healthcareRepository.getInabilityReceipt(eSickRequestDTO)
                if (response.isSuccessful) {
                    _inabilityReceiptLiveData.postValue(response)
                } else {
                    inabilityReceiptErrorLiveData.postValue("${context?.getString(R.string.error)}: ${response.message()}")
                }
            } catch (e: Exception) {
                inabilityReceiptErrorLiveData.postValue("${context?.getString(R.string.exception)}: ${e.message}")
            }
        }
    }

    fun getInabilityReport(eSickRequestDTO: ESickRequestDTO) {
        viewModelScope.launch {
            try {
                val response = healthcareRepository.getInabilityReport(eSickRequestDTO)
                if (response.isSuccessful) {
                    _inabilityReportLiveData.postValue(response)
                } else {
                    inabilityReportErrorLiveData.postValue("${context?.getString(R.string.error)}: ${response.message()}")
                }
            } catch (e: Exception) {
                inabilityReportErrorLiveData.postValue("${context?.getString(R.string.exception)}: ${e.message}")
            }
        }
    }

    fun getSickLeaveReport(eSickRequestDTO: ESickRequestDTO) {
        viewModelScope.launch {
            try {
                val response = healthcareRepository.getSickLeaveReport(eSickRequestDTO)
                if (response.isSuccessful) {
                    _getSickReportLiveData.postValue(response)
                } else {
                    getSickReportErrorLiveData.postValue("${context?.getString(R.string.error)}: ${response.message()}")
                }
            } catch (e: Exception) {
                getSickReportErrorLiveData.postValue("${context?.getString(R.string.exception)}: ${e.message}")
            }
        }
    }

    fun getDownloadCommisionDecission(eSickRequestDTO: ESickRequestDTO) {
        viewModelScope.launch {
            try {
                val response = healthcareRepository.getDownloadCommisionDecission(eSickRequestDTO)
                if (response.isSuccessful) {
                    _getSickReportLiveData.postValue(response)
                } else {
                    getSickReportErrorLiveData.postValue("${context?.getString(R.string.error)}: ${response.message()}")
                }
            } catch (e: Exception) {
                getSickReportErrorLiveData.postValue("${context?.getString(R.string.exception)}: ${e.message}")
            }
        }
    }

    fun getCasesData(medicalCartonRequest: MedicalCartonRequest) {
        viewModelScope.launch {
            try {
                getMedicalCardLiveData.value = null
                val response = healthcareRepository.getCasesData(medicalCartonRequest)
                if (response.isSuccessful) {
                    getMedicalCardLiveData.value = response
                } else {
                    getMedicalCardErrorLiveData.postValue("${context?.getString(R.string.error)}: ${response.message()}")
                }
            } catch (e: Exception) {
                getMedicalCardErrorLiveData.postValue("${context?.getString(R.string.exception)}: ${e.message}")
            }
        }
    }

    fun downloadReportMedicalCard(medicalCartonPdfRequest: MedicalCartonPdfRequest) {
        viewModelScope.launch {
            try {
                val response =
                    healthcareRepository.downloadReportMedicalCard(medicalCartonPdfRequest)
                if (response.isSuccessful) {
                    reportMedicalCardLiveData.value = response
                } else {
                    reportMedicalCardErrorLiveData.postValue("${context?.getString(R.string.error)}: ${response.message()}")
                }
            } catch (e: Exception) {
                getMedicalCardErrorLiveData.postValue("${context?.getString(R.string.exception)}: ${e.message}")
            }
        }
    }

    fun getListInstitutions(medicalCartonRequest: MedicalCartonRequest) {
        viewModelScope.launch {
            try {
                val response = healthcareRepository.getListInstitutions(medicalCartonRequest)
                if (response.isSuccessful) {
                    getInstitutionsLiveData.value = response
                } else {
                    getInstitutionsErrorLiveData.postValue("${context?.getString(R.string.error)}: ${response.message()}")
                }
            } catch (e: Exception) {
                getInstitutionsErrorLiveData.postValue("${context?.getString(R.string.exception)}: ${e.message}")
            }
        }
    }

    fun getAccessMedicalCarton(medicalCartonRequest: MedicalCartonRequest) {
        viewModelScope.launch {
            try {
                val response = healthcareRepository.getAccessMedicalCarton(medicalCartonRequest)
                if (response.isSuccessful) {
                    getAccessMedicalCartonLiveData.value = response
                } else {
                    getAccessMedicalCartonErrorLiveData.postValue("${context?.getString(R.string.error)}: ${response.message()}")
                }
            } catch (e: Exception) {
                getAccessMedicalCartonErrorLiveData.postValue("${context?.getString(R.string.exception)}: ${e.message}")
            }
        }
    }

    fun getInstitutions(medicalCartonRequest: MedicalCartonRequest) {
        viewModelScope.launch {
            try {
                val response = healthcareRepository.getInstitutions(medicalCartonRequest)
                if (response.isSuccessful) {
                    getInstitutionsArrLiveData.value = response
                } else {
                    getInstitutionsArrErrorLiveData.postValue("${context?.getString(R.string.error)}: ${response.message()}")
                }
            } catch (e: Exception) {
                getInstitutionsArrErrorLiveData.postValue("${context?.getString(R.string.exception)}: ${e.message}")
            }
        }
    }

    fun refreshInstitutionData(medicalCartonRequest: MedicalCartonRequest) {
        viewModelScope.launch {
            try {
                val response = healthcareRepository.refreshInstitutionData(medicalCartonRequest)
                if (response.isSuccessful) {
                    getRefreshInstitutionDataLiveData.value = response
                } else {
                    getRefreshInstitutionDataErrorLiveData.postValue("${context?.getString(R.string.error)}: ${response.message()}")
                }
            } catch (e: Exception) {
                getRefreshInstitutionDataErrorLiveData.postValue("${context?.getString(R.string.exception)}: ${e.message}")
            }
        }
    }

    fun getCommonsThemes(commonsThemesRequest: CommonsThemesRequest) {
        viewModelScope.launch {
            try {
                val response = healthcareRepository.getCommonsThemes(commonsThemesRequest)
                if (response.isSuccessful) {
                    getCommonsThemesLiveData.value = response
                } else {
                    getCommonsThemesErrorLiveData.postValue("${context?.getString(R.string.error)}: ${response.message()}")
                }
            } catch (e: Exception) {
                getCommonsThemesErrorLiveData.postValue("${context?.getString(R.string.exception)}: ${e.message}")
            }
        }
    }

    fun getLikes(commonsThemesRequest: CommonsThemesRequest) {
        viewModelScope.launch {
            try {
                getLikesLiveData == null
                val response = healthcareRepository.getLikes(commonsThemesRequest)
                if (response.isSuccessful) {
                    getLikesLiveData.value = response
                } else {
                    getCommonsThemesErrorLiveData.postValue("${context?.getString(R.string.error)}: ${response.message()}")
                }
            } catch (e: Exception) {
                getLikesErrorLiveData.postValue("${context?.getString(R.string.exception)}: ${e.message}")
            }
        }
    }

    fun followSocialGroup(commonsThemesRequest: CommonsThemesRequest) {
        viewModelScope.launch {
            try {
                val response = healthcareRepository.followSocialGroup(commonsThemesRequest)
                if (response.isSuccessful) {
                    followSocialGroupLiveData.value = response
                } else {
                    followSocialGroupErrorLiveData.postValue("${context?.getString(R.string.error)}: ${response.message()}")
                }
            } catch (e: Exception) {
                followSocialGroupErrorLiveData.postValue("${context?.getString(R.string.exception)}: ${e.message}")
            }
        }
    }

    fun getHealthInstitutionQA(pin: String) {
        viewModelScope.launch {
            try {
                val response = healthcareRepository.getHealthInstitutionQA(pin)
                if (response.isSuccessful) {
                    getHealthInstitutionLiveData.value = response
                } else {
                    getHealthInstitutionErrorLiveData.postValue("${context?.getString(R.string.error)}: ${response.message()}")
                }
            } catch (e: Exception) {
                getHealthInstitutionErrorLiveData.postValue("${context?.getString(R.string.exception)}: ${e.message}")
            }
        }
    }

    fun getCategoryQA(pin: String) {
        viewModelScope.launch {
            try {
                val response = healthcareRepository.getCategoryQA(pin)
                if (response.isSuccessful) {
                    getCategoryQALiveData.value = response
                } else {
                    getCategoryQAErrorLiveData.postValue("${context?.getString(R.string.error)}: ${response.message()}")
                }
            } catch (e: Exception) {
                getCategoryQAErrorLiveData.postValue("${context?.getString(R.string.exception)}: ${e.message}")
            }
        }
    }

    fun getTopicQA(pin: String) {
        viewModelScope.launch {
            try {
                val response = healthcareRepository.getTopicQA(pin)
                if (response.isSuccessful) {
                    getTopicQALiveData.value = response
                } else {
                    getTopicQAErrorLiveData.postValue("${context?.getString(R.string.error)}: ${response.message()}")
                }
            } catch (e: Exception) {
                getTopicQAErrorLiveData.postValue("${context?.getString(R.string.exception)}: ${e.message}")
            }
        }
    }

    fun listAllQA(pin: String) {
        viewModelScope.launch {
            try {
                val response = healthcareRepository.listAllQA(pin)
                if (response.isSuccessful) {
                    getAllQALiveData.value = response
                } else {
                    getAllQAErrorLiveData.postValue("${context?.getString(R.string.error)}: ${response.message()}")
                }
            } catch (e: Exception) {
                getAllQAErrorLiveData.postValue("${context?.getString(R.string.exception)}: ${e.message}")
            }
        }
    }

    fun getRecommendations(preventionDTO: PreventionDTO) {
        viewModelScope.launch {
            try {
                val response = healthcareRepository.getRecommendations(preventionDTO)
                if (response.isSuccessful) {
                    getRecommendationLiveData.value = response
                } else {
                    getAllQAErrorLiveData.postValue("${context?.getString(R.string.error)}: ${response.message()}")
                }
            } catch (e: Exception) {
                getRecommendationErrorLiveData.postValue("${context?.getString(R.string.exception)}: ${e.message}")
            }
        }
    }

    fun getMedicaments(preventionDTO: PreventionDTO) {
        viewModelScope.launch {
            try {
                val response = healthcareRepository.getMedicaments(preventionDTO)
                if (response.isSuccessful) {
                    getMedicamenteLiveData.value = response
                } else {
                    getMedicamenteErrorLiveData.postValue("${context?.getString(R.string.error)}: ${response.message()}")
                }
            } catch (e: Exception) {
                getMedicamenteErrorLiveData.postValue("${context?.getString(R.string.exception)}: ${e.message}")
            }
        }
    }

    fun getMKB10(pin: PinRequest) {
        viewModelScope.launch {
            try {
                val response = healthcareRepository.getMKB10(pin)
                if (response.isSuccessful) {
                    getMKB10LiveData.value = response
                } else {
                    getMKB10ErrorLiveData.postValue("${context?.getString(R.string.error)}: ${response.message()}")
                }
            } catch (e: Exception) {
                getMKB10ErrorLiveData.postValue("${context?.getString(R.string.exception)}: ${e.message}")
            }
        }
    }

    fun getSubMKB10(preventionDTO: PreventionDTO) {
        viewModelScope.launch {
            try {
                val response = healthcareRepository.getSubMKB10(preventionDTO)
                if (response.isSuccessful) {
                    getMKB10SubLiveData.value = response
                } else {
                    getMKB10SubErrorLiveData.postValue("${context?.getString(R.string.error)}: ${response.message()}")
                }
            } catch (e: Exception) {
                getMKB10SubErrorLiveData.postValue("${context?.getString(R.string.exception)}: ${e.message}")
            }
        }
    }

    fun getSubMKB10Groups(preventionDTO: PreventionDTO) {
        viewModelScope.launch {
            try {
                val response = healthcareRepository.getSubMKB10Groups(preventionDTO)
                if (response.isSuccessful) {
                    getMKB10SubGroupsLiveData.value = response
                } else {
                    getMKB10SubGroupsErrorLiveData.postValue("${context?.getString(R.string.error)}: ${response.message()}")
                }
            } catch (e: Exception) {
                getMKB10SubGroupsErrorLiveData.postValue("${context?.getString(R.string.exception)}: ${e.message}")
            }
        }
    }

    fun getFutureExams(pinRequest: PinRequest) {
        viewModelScope.launch {
            try {
                val response = healthcareRepository.getFutureExams(pinRequest)
                if (response.isSuccessful) {
                    getFutureExamsLiveData.value = response
                } else {
                    getFutureExamsErrorLiveData.postValue("${context?.getString(R.string.error)}: ${response.message()}")
                }
            } catch (e: Exception) {
                getFutureExamsErrorLiveData.postValue("${context?.getString(R.string.exception)}: ${e.message}")
            }
        }
    }

    fun getPastScheduledAppointments(eventHistoryDataRequest: EventHistoryDataRequest) {
        viewModelScope.launch {
            try {
                val response =
                    healthcareRepository.getPastScheduledAppointments(eventHistoryDataRequest)
                if (response.isSuccessful) {
                    getPastScheduledLiveData.value = response
                } else {
                    getPastScheduledErrorLiveData.postValue("${context?.getString(R.string.error)}: ${response.message()}")
                }
            } catch (e: Exception) {
                getPastScheduledErrorLiveData.postValue("${context?.getString(R.string.exception)}: ${e.message}")
            }
        }
    }

    fun deleteExam(examDeleteRequest: ExamDeleteRequest) {
        viewModelScope.launch {
            try {
                val response = healthcareRepository.deleteExam(examDeleteRequest)
                if (response.isSuccessful) {
                    deleteExamLiveData.value = response
                } else {
                    deleteExamErrorLiveData.postValue("${context?.getString(R.string.error)}: ${response.message()}")
                }
            } catch (e: Exception) {
                deleteExamErrorLiveData.postValue("${context?.getString(R.string.exception)}: ${e.message}")
            }
        }
    }

    fun getAllMedicines() {
        viewModelScope.launch {
            try {
                val response = healthcareRepository.getAllMedicines()
                if (response.isSuccessful) {
                    getIteractionMedicineLiveData.value = response
                } else {
                    getIteractionMedicineErrorLiveData.postValue("${context?.getString(R.string.error)}: ${response.message()}")
                }
            } catch (e: Exception) {
                getIteractionMedicineErrorLiveData.postValue("${context?.getString(R.string.exception)}: ${e.message}")
            }
        }
    }

    fun getStaticRag(patientId: Int, staticRagRequest: StaticRagRequest) {
        viewModelScope.launch {
            try {
                val response = healthcareRepository.getStaticRag(patientId, staticRagRequest)
                if (response.isSuccessful) {
                    getStaticRagLiveData.value = response
                } else {
                    getStaticRagErrorLiveData.postValue("${context?.getString(R.string.error)}: ${response.message()}")
                }
            } catch (e: Exception) {
                getStaticRagErrorLiveData.postValue("${context?.getString(R.string.exception)}: ${e.message}")
            }
        }
    }

    fun saveFeedback(patientId: Int, feedbackRequest: FeedbackRequest) {
        getSaveFeedbackLiveData.postValue(null) // 🧼 Reset pre novog poziva
        viewModelScope.launch {
            try {
                val response = healthcareRepository.saveFeedback(patientId, feedbackRequest)
                if (response.isSuccessful) {
                    getSaveFeedbackLiveData.value = response
                } else {
                    getSaveFeedbackErrorLiveData.postValue("${context?.getString(R.string.error)}: ${response.message()}")
                }
            } catch (e: Exception) {
                getSaveFeedbackErrorLiveData.postValue("${context?.getString(R.string.exception)}: ${e.message}")
            }
        }
    }
}
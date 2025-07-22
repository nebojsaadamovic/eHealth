package com.example.zdravlje.data.model.response.healthcare

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class PrescribedListMedicamentResponse(
    @SerializedName("patmed_id")
    val patmedId: String? = null,
    @SerializedName("cbmed_id")
    val medicamentId: String? = null,
    @SerializedName("patmed_prescribed")
    val prescribed: String? = null,
    @SerializedName("cbmed_protected_name")
    val medicamentProtectedName: String? = null,
    @SerializedName("cbmedalims_naziv_leka")
    val medicamentName: String? = null,
    @SerializedName("cbmed_dosage_strength")
    val strength: String? = null,
    @SerializedName("cbmed_pharmaceutical_form")
    val pharmaceuticalForm: String? = null,
    @SerializedName("cbmedalims_oblik_i_doza_leka")
    val dosage: String? = null,
    @SerializedName("patmed_time_end")
    val expiryDate: Long,
    @SerializedName("patmed_time_start")
    val startDate: Long,
    @SerializedName("patmed_status")
    val status: String? = null,
    @SerializedName("patmed_tip_recepta")
    val prescriptionType: String? = null,
    @SerializedName("cbmed_inn")
    val inn: String? = null,
    @SerializedName("cbmedalims_proizvodjac")
    val companyMaker: String? = null,
    @SerializedName("cbmedalims_nosilac_dozvole")
    val licenseHolder: String? = null,
    @SerializedName("cbmed_atc")
    val atc: String? = null,
    @SerializedName("cbmedalims_vrsta_leka")
    val typeMedicamente: String? = null,
    @SerializedName("cbmed_jkl")
    val jkl: String? = null,
    @SerializedName("cbmedalims_rezim_izdavanja_leka")
    val regimeDispensing: String? = null,
    @SerializedName("cbmedrfzo_naziv_liste")
    val listName: String? = null,
    @SerializedName("cbmedrfzo_cenaleka")
    val priceMedicamenete: String? = null,
    @SerializedName("medic_summary_of_drug_characteristics")
    val characteristicsMedicamente: String? = null,
    @SerializedName("medic_patient_instruction")
    val instructionManual: String? = null,
    @SerializedName("cbmedrfzo_opisdijagnoze")
    val descriptionDagnosis: String? = null,
    @SerializedName("cbmeddet_participation")
    val participation: String? = null,
    @SerializedName("cbmedrfzo_godineod")
    val yearsFrom: String? = null,
    @SerializedName("cbmedrfzo_godinedo")
    val yearsTo: String? = null,
    @SerializedName("cbmedrfzo_kolicinaleka")
    val quantity: String? = null,
    @SerializedName("cbmedrfzo_procenatucesca")
    val cbmedrfzoProcenatucesca: String? = null,
    @SerializedName("patmed_notes")
    val patmedNotes: String? = null,
    @SerializedName("patmed_rcpt_status")
    val patmed_rcpt_status: String? = null,
    @SerializedName("cbmedalims_datum_vazenja_resenja")
    val cbmedalimsDecisionValidityDate: String ?=null,
    @SerializedName("pin")
    val pin: String? = null,
) : Parcelable

data class DiagnosisEntry(
    val diagnosis: String,
    val participation: String,
    val quantity: String,
    val yearsFrom: String,
    val yearsTo: String
)

data class PatmedNotes(
    val createDate: String?,
    val arrivalDate: String?,
    val issuingUser: String?,
    val pharmacy: String?,
    val pharmacist: String?,
    val drugCode: String?,
    val medicamentDescription: String?,
    val medicamentPackageForm: String?,
    val manufacturer: String?,
    val drugPrimaryPackageForm: String?,
    val drugStrength: String?,
    val dosage: String?,
    val quantity: String?,
    val prescriptionDiagnosis: String?,
    val prescriptionTypeName: String?,
)
package com.example.zdravlje.data.model.response.healthcare

data class SickLeaveResponse(
    val id: Int,
    val tranId: Int,
    val isExtended: Boolean,
    val sickManagementStatus: String,
    val startDate: String,
    val controlDate: String,
    val endDate: String,
    val numberOfSickDays: Int,
    val sickReason: String,
    val diagnoses: List<DiagnosisResponse>,
    val commissionDecisions: List<CommissionDecision>,
    val employers: List<Employer>
)

data class DiagnosisResponse(
    val code: String,
    val name: String,
    val diagnosisId: Int,
    val showOnConfirmationReport: Boolean,
    val showOnSickNoteReport: Boolean,
    val diagnosisTypeId: Int,
    val isMainDiagnosis: Boolean
)

data class CommissionDecision(
    val commissionDecisionId: Int,
    val sickManagementId: Int,
    val rfzoCaseNumber: String,
    val branchOfficeCode: String,
    val numberOfDecision: String,
    val comissionTypeId: Int,
    val dateOfDecision: String,
    val decisionTypeId: Int,
    val healthCareInstitutionOfApplicant: String?,
    val referredToHealthCareInstitution: String?,
    val comissionPlace: String,
    val previousNumberOfCommission: String?,
    val previousDateProcessedCommission: String?,
    val previousObjection: String?,
    val nameOfHealthCareWhichToReferred: String?,
    val statusOfProcessedId: Int,
    val supplementaryDocumentation: String?,
    val evaluationOfTheMedicalCommissionId: Int,
    val sickLeaveReasonCode: String,
    val sickLeaveReasonName: String,
    val additionalSickLeaveReasonCode: String,
    val additionalSickLeaveReasonName: String?,
    val branchOfficeOutpostCode: String,
    val urlForCommissionDecision: String
)

data class Employer(
    val employerId: Int,
    val employerName: String,
    val urlForTempInabilityReceipt: String,
    val sickLeaveNotes: List<SickLeaveNoteResponse>
)

data class SickLeaveNoteResponse(
    val urlForTempInabilityReport: String,
    val permanentWorkDisabilityFrom: String,
    val permanentWorkDisabilityTo: String,
    val sickNoteTypeTranslate: String,
    val diagnosisNameStart: String,
    val diagnosisNameEnd: String,
    val sickNoteID: Int,
    val sickManagementID: Int,
    val sickNoteType: String,
    val diagnosisCodeIDStart: Int,
    val diagnosisCodeIDEnd: Int,
    val workDisabilityTotal: Int,
    val generatedManually: Boolean,
    val sickNoteSignatureID: Int
)

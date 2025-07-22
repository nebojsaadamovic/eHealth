package com.example.zdravlje.data.model.response.healthcare

import com.google.gson.annotations.SerializedName
import java.math.RoundingMode

data class MedicamenteResponse(
    @SerializedName("cbmed_id")
    val cbmedId: Int? = null,
    @SerializedName("cbmed_jkl")
    val cbmedJkl: String? = null,
    @SerializedName("cbmed_atc")
    val cbmedAtc: String? = null,
    @SerializedName("cbmed_inn")
    val cbmedInn: String? = null,
    @SerializedName("cbmed_protected_name")
    val cbmedProtectedName: String? = null,
    @SerializedName("cbmed_pharmaceutical_form")
    val cbmedPharmaceuticalForm: String? = null,
    @SerializedName("cbmed_dosage_strength")
    val cbmedDosageStrength: String? = null,
    @SerializedName("cbmed_status")
    val cbmedStatus: Int? = null,
    @SerializedName("cbmed_list")
    val cbmedList: String? = null,
    @SerializedName("cbmeddet_id")
    val cbmeddetId: Int? = null,
    @SerializedName("cbmeddet_cbmed_id")
    val cbmeddetCbmedId: Int? = null,
    @SerializedName("cbmeddet_company")
    val cbmeddetCompany: String? = null,
    @SerializedName("cbmeddet_state")
    val cbmeddetState: String? = null,
    @SerializedName("cbmeddet_ddd")
    val cbmeddetDdd: String? = null,
    @SerializedName("cbmeddet_participation")
    val cbmeddetParticipation: String? = null,
    @SerializedName("cbmeddet_notice")
    val cbmeddetNotice: String? = null,
    @SerializedName("cbmeddet_atc_iv")
    val cbmeddetAtcIv: String? = null,
    @SerializedName("cbmeddet_atc_iv_title")
    val cbmeddetAtcIvTitle: String? = null,
    @SerializedName("cbmeddet_atc_iii")
    val cbmeddetAtcIii: String? = null,
    @SerializedName("cbmeddet_atc_iii_title")
    val cbmeddetAtcIiiTitle: String? = null,
    @SerializedName("cbmeddet_atc_ii")
    val cbmeddetAtcIi: String? = null,
    @SerializedName("cbmeddet_atc_ii_title")
    val cbmeddetAtcIiTitle: String? = null,
    @SerializedName("cbmeddet_atc_i")
    val cbmeddetAtcI: String? = null,
    @SerializedName("cbmeddet_atc_i_title")
    val cbmeddetAtcITitle: String? = null,
    @SerializedName("cbmedalims_id")
    val cbmedalimsId: String? = null,
    @SerializedName("cbmedalims_medicine_id")
    val cbmedalimsMedicineId: Int? = null,
    @SerializedName("cbmedalims_vrsta_resenja")
    val cbmedalimsVrstaResenja: String? = null,
    @SerializedName("cbmedalims_naziv_leka")
    val cbmedalimsNazivLeka: String? = null,
    @SerializedName("cbmedalims_inn")
    val cbmedalimsInn: String? = null,
    @SerializedName("cbmedalims_rezim_izdavanja_leka")
    val cbmedalimsRezimIzdavanjaLeka: String? = null,
    @SerializedName("cbmedalims_oblik_i_doza_leka")
    val cbmedalimsOblikIDozaLeka: String? = null,
    @SerializedName("cbmedalims_broj_resenja_o_stavljanju_leka_u_promet")
    val cbmedalimsBrojResenjaOStavljanjuLekaUPromet: String? = null,
    @SerializedName("cbmedalims_datum_resenja_o_stavljanju_leka_u_promet")
    val cbmedalimsDatumResenjaOStavljanjuLekaUPromet: String? = null,
    @SerializedName("cbmedalims_datum_vazenja_resenja")
    val cbmedalimsDatumVazenjaResenja: String? = null,
    @SerializedName("cbmedalims_proizvodjac")
    val cbmedalimsProizvodjac: String? = null,
    @SerializedName("cbmedalims_nosilac_dozvole")
    val cbmedalimsNosilacDozvole: String? = null,
    @SerializedName("cbmedalims_atc")
    val cbmedalimsAtc: String? = null,
    @SerializedName("cbmedalims_ean")
    val cbmedalimsEan: String? = null,
    @SerializedName("cbmedalims_jkl")
    val cbmedalimsJkl: String? = null,
    @SerializedName("cbmedalims_vrsta_leka")
    val cbmedalimsVrstaLeka: String? = null,
    @SerializedName("cbmedalims_sifra_proizvoda")
    val cbmedalimsSifraProizvoda: String? = null,
    @SerializedName("cbmedalims_sifra_proizvodjaca_u_saradnji")
    val cbmedalimsSifraProizvodjacaUSaradnji: String? = null,
    @SerializedName("cbmedalims_oblik_saradnje")
    val cbmedalimsOblikSaradnje: String? = null,
    @SerializedName("cbmedalims_sifra_proizvodjaca")
    val cbmedalimsSifraProizvodjaca: String? = null,
    @SerializedName("cbmedalims_sifra_nosioca_dozvole")
    val cbmedalimsSifraNosiocaDozvole: String? = null,
    @SerializedName("cbmedrfzo_id")
    val cbmedrfzoId: Int? = null,
    @SerializedName("cbmedrfzo_medicine_id")
    val cbmedrfzoMedicineId: Int? = null,
    @SerializedName("cbmedrfzo_id_lek")
    val cbmedrfzoIdLek: Int? = null,
    @SerializedName("cbmedrfzo_jkl")
    val cbmedrfzoJkl: String? = null,
    @SerializedName("cbmedrfzo_cenaleka")
    val cbmedrfzoCenaLeka: String? = null,
    @SerializedName("cbmedrfzo_porocporeza")
    val cbmedrfzoPoroCporeza: String? = null,
    @SerializedName("cbmedrfzo_id_lista")
    val cbmedrfzoIdLista: String? = null,
    @SerializedName("cbmedrfzo_naziv_liste")
    val cbmedrfzoNazivListe: String? = null,
    @SerializedName("cbmedrfzo_opisdijagnoze")
    val cbmedrfzoOpisDijagnoze: String? = null,
    @SerializedName("cbmedrfzo_procenatucesca")
    val cbmedrfzoProcenatUcesca: String? = null,
    @SerializedName("cbmedrfzo_godineod")
    val cbmedrfzoGodineOd: String? = null,
    @SerializedName("cbmedrfzo_godinedo")
    val cbmedrfzoGodineDo: String? = null,
    @SerializedName("cbmedrfzo_kolicinaleka")
    val cbmedrfzoKolicinaLeka: String? = null,
    @SerializedName("cbmedrfzo_procenatmarze")
    val cbmedrfzoProcenatMarze: String? = null
) {
    fun getRoundedPriceMedicamente(): String? {
        return cbmedrfzoCenaLeka?.toBigDecimalOrNull()?.setScale(2, RoundingMode.HALF_UP)?.toPlainString()
    }
}

package com.example.android.politicalpreparedness.network.models.response

import com.example.android.politicalpreparedness.network.models.Office
import com.example.android.politicalpreparedness.network.models.Official

data class RepresentativeResponse(
    val offices: List<Office>,
    val officials: List<Official>
)

enum class RepresentativeLevels(val level: String) {
    Administrative_Area1("administrativeArea1"),
    Administrative_Area2("administrativeArea2"),
    Country("country"),
    International("international"),
    Locality("locality"),
    Level("regional"),
    Special("special"),
    Sublocality_1("subLocality1"),
    Sublocality_2("subLocality2")
}

enum class RepresentativeRoles(val role: String) {
    DeputyHeadOfGovernement("deputyHeadOfGovernment"),
    ExecutiveCouncil("executiveCouncil"),
    GovernmentOfficer("governmentOfficer"),
    HeadOfGovernement("headOfGovernment"),
    HeadOfState("headOfState"),
    HighetsCourtJudge("highestCourtJudge"),
    Judge("judge"),
    Legislator_LowerBody("legislatorLowerBody"),
    Legislator_UpperBody("legislatorUpperBody"),
    SchoolBoard("schoolBoard"),
    SpecialPurposeOfficer("specialPurposeOfficer")
}
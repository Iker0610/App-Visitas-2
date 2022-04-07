package das.omegaterapia.visits.data


import das.omegaterapia.visits.model.entities.VisitCard
import das.omegaterapia.visits.model.entities.VisitData
import java.time.LocalDateTime
import java.time.ZoneId
import kotlin.random.Random

val getYear = { 2022 } // { Random.nextInt(2018, 2022) }
val getMonth = { Random.nextInt(3, 5) }
val getDay = { Random.nextInt(1, 30) }
val getHour = { Random.nextInt(8, 20) }
val getMinute = { arrayOf(0, 15, 30, 45)[Random.nextInt(0, 4)] }
val getVisitDate = { LocalDateTime.of(getYear(), getMonth(), getDay(), getHour(), getMinute()).atZone(ZoneId.systemDefault()) }

val getRandomClient = { clients[Random.nextInt(clients.size)] }
val getRandomCompanion = { clients[Random.nextInt(clients.size)].toString() }

const val observation =
    "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nam felis elit, mattis scelerisque orci sed, consequat facilisis leo. In at mi diam. In at vestibulum massa. Nulla viverra, magna quis auctor blandit, dui massa efficitur neque, nec placerat felis mauris non purus. Etiam vestibulum accumsan tristique. Vivamus aliquet dui nec nulla eleifend, in rutrum nulla blandit. Suspendisse et elementum elit. Sed varius mattis facilisis. "

val visitList = listOf(

    VisitCard(
        client = getRandomClient(),
        visitData = VisitData(
            mainClientPhone = "",
            companions = listOf(getRandomCompanion(), getRandomCompanion(), getRandomCompanion()),
            visitDate = getVisitDate(),
            observations = observation,
            isVIP = true
        )
    ),

    VisitCard(
        client = getRandomClient(),
        visitData = VisitData(
            mainClientPhone = "",
            visitDate = getVisitDate(),
        )
    ),

    VisitCard(
        client = getRandomClient(),
        visitData = VisitData(
            mainClientPhone = "",
            visitDate = getVisitDate(),
            observations = observation
        )
    ),

    VisitCard(
        client = getRandomClient(),
        visitData = VisitData(
            mainClientPhone = "",
            companions = listOf(getRandomCompanion(), getRandomCompanion()),
            visitDate = getVisitDate(),
            isVIP = true
        )
    ),

    VisitCard(
        client = getRandomClient(),
        visitData = VisitData(
            mainClientPhone = "",
            companions = listOf(getRandomCompanion()),
            visitDate = getVisitDate(),
            observations = observation
        )
    ),

    VisitCard(
        client = getRandomClient(),
        visitData = VisitData(
            mainClientPhone = "",
            companions = listOf(getRandomCompanion()),
            visitDate = getVisitDate(),
        )
    ),
    VisitCard(
        client = getRandomClient(),
        visitData = VisitData(
            mainClientPhone = "",
            companions = listOf(getRandomCompanion(), getRandomCompanion(), getRandomCompanion()),
            visitDate = getVisitDate(),
            observations = observation,
            isVIP = true
        )
    ),

    VisitCard(
        client = getRandomClient(),
        visitData = VisitData(
            mainClientPhone = "",
            visitDate = getVisitDate(),
        )
    ),

    VisitCard(
        client = getRandomClient(),
        visitData = VisitData(
            mainClientPhone = "",
            visitDate = getVisitDate(),
            observations = observation,
            isVIP = true
        )
    ),

    VisitCard(
        client = getRandomClient(),
        visitData = VisitData(
            mainClientPhone = "",
            companions = listOf(getRandomCompanion(), getRandomCompanion()),
            visitDate = getVisitDate(),
        )
    ),

    VisitCard(
        client = getRandomClient(),
        visitData = VisitData(
            mainClientPhone = "",
            companions = listOf(getRandomCompanion()),
            visitDate = getVisitDate(),
            observations = observation,
            isVIP = true
        )
    ),

    VisitCard(
        client = getRandomClient(),
        visitData = VisitData(
            mainClientPhone = "",
            companions = listOf(getRandomCompanion()),
            visitDate = getVisitDate(),
            isVIP = true
        )
    ),

    VisitCard(
        client = getRandomClient(),
        visitData = VisitData(
            mainClientPhone = "",
            companions = listOf(getRandomCompanion(), getRandomCompanion(), getRandomCompanion()),
            visitDate = getVisitDate(),
            observations = observation,
            isVIP = true
        )
    ),

    VisitCard(
        client = getRandomClient(),
        visitData = VisitData(
            mainClientPhone = "",
            visitDate = getVisitDate(),
        )
    ),

    VisitCard(
        client = getRandomClient(),
        visitData = VisitData(
            mainClientPhone = "",
            visitDate = getVisitDate(),
            observations = observation
        )
    ),

    VisitCard(
        client = getRandomClient(),
        visitData = VisitData(
            mainClientPhone = "",
            companions = listOf(getRandomCompanion(), getRandomCompanion()),
            visitDate = getVisitDate(),
            isVIP = true
        )
    ),

    VisitCard(
        client = getRandomClient(),
        visitData = VisitData(
            mainClientPhone = "",
            companions = listOf(getRandomCompanion()),
            visitDate = getVisitDate(),
            observations = observation
        )
    ),

    VisitCard(
        client = getRandomClient(),
        visitData = VisitData(
            mainClientPhone = "",
            companions = listOf(getRandomCompanion()),
            visitDate = getVisitDate(),
        )
    ),
    VisitCard(
        client = getRandomClient(),
        visitData = VisitData(
            mainClientPhone = "",
            companions = listOf(getRandomCompanion(), getRandomCompanion(), getRandomCompanion()),
            visitDate = getVisitDate(),
            observations = observation,
            isVIP = true
        )
    ),

    VisitCard(
        client = getRandomClient(),
        visitData = VisitData(
            mainClientPhone = "",
            visitDate = getVisitDate(),
        )
    ),

    VisitCard(
        client = getRandomClient(),
        visitData = VisitData(
            mainClientPhone = "",
            visitDate = getVisitDate(),
            observations = observation,
            isVIP = true
        )
    ),

    VisitCard(
        client = getRandomClient(),
        visitData = VisitData(
            mainClientPhone = "",
            companions = listOf(getRandomCompanion(), getRandomCompanion()),
            visitDate = getVisitDate(),
        )
    ),

    VisitCard(
        client = getRandomClient(),
        visitData = VisitData(
            mainClientPhone = "",
            companions = listOf(getRandomCompanion()),
            visitDate = getVisitDate(),
            observations = observation,
            isVIP = true
        )
    ),

    VisitCard(
        client = getRandomClient(),
        visitData = VisitData(
            mainClientPhone = "",
            companions = listOf(getRandomCompanion()),
            visitDate = getVisitDate(),
            isVIP = true
        )
    ),


    VisitCard(
        client = getRandomClient(),
        visitData = VisitData(
            mainClientPhone = "",
            companions = listOf(getRandomCompanion(), getRandomCompanion(), getRandomCompanion()),
            visitDate = getVisitDate(),
            observations = observation,
            isVIP = true
        )
    ),

    VisitCard(
        client = getRandomClient(),
        visitData = VisitData(
            mainClientPhone = "",
            visitDate = getVisitDate(),
        )
    ),

    VisitCard(
        client = getRandomClient(),
        visitData = VisitData(
            mainClientPhone = "",
            visitDate = getVisitDate(),
            observations = observation
        )
    ),

    VisitCard(
        client = getRandomClient(),
        visitData = VisitData(
            mainClientPhone = "",
            companions = listOf(getRandomCompanion(), getRandomCompanion()),
            visitDate = getVisitDate(),
            isVIP = true
        )
    ),

    VisitCard(
        client = getRandomClient(),
        visitData = VisitData(
            mainClientPhone = "",
            companions = listOf(getRandomCompanion()),
            visitDate = getVisitDate(),
            observations = observation
        )
    ),

    VisitCard(
        client = getRandomClient(),
        visitData = VisitData(
            mainClientPhone = "",
            companions = listOf(getRandomCompanion()),
            visitDate = getVisitDate(),
        )
    ),
    VisitCard(
        client = getRandomClient(),
        visitData = VisitData(
            mainClientPhone = "",
            companions = listOf(getRandomCompanion(), getRandomCompanion(), getRandomCompanion()),
            visitDate = getVisitDate(),
            observations = observation,
            isVIP = true
        )
    ),

    VisitCard(
        client = getRandomClient(),
        visitData = VisitData(
            mainClientPhone = "",
            visitDate = getVisitDate(),
        )
    ),

    VisitCard(
        client = getRandomClient(),
        visitData = VisitData(
            mainClientPhone = "",
            visitDate = getVisitDate(),
            observations = observation,
            isVIP = true
        )
    ),

    VisitCard(
        client = getRandomClient(),
        visitData = VisitData(
            mainClientPhone = "",
            companions = listOf(getRandomCompanion(), getRandomCompanion()),
            visitDate = getVisitDate(),
        )
    ),

    VisitCard(
        client = getRandomClient(),
        visitData = VisitData(
            mainClientPhone = "",
            companions = listOf(getRandomCompanion()),
            visitDate = getVisitDate(),
            observations = observation,
            isVIP = true
        )
    ),

    VisitCard(
        client = getRandomClient(),
        visitData = VisitData(
            mainClientPhone = "",
            companions = listOf(getRandomCompanion()),
            visitDate = getVisitDate(),
            isVIP = true
        )
    ),

    VisitCard(
        client = getRandomClient(),
        visitData = VisitData(
            mainClientPhone = "",
            companions = listOf(getRandomCompanion(), getRandomCompanion(), getRandomCompanion()),
            visitDate = getVisitDate(),
            observations = observation,
            isVIP = true
        )
    ),

    VisitCard(
        client = getRandomClient(),
        visitData = VisitData(
            mainClientPhone = "",
            visitDate = getVisitDate(),
        )
    ),

    VisitCard(
        client = getRandomClient(),
        visitData = VisitData(
            mainClientPhone = "",
            visitDate = getVisitDate(),
            observations = observation
        )
    ),

    VisitCard(
        client = getRandomClient(),
        visitData = VisitData(
            mainClientPhone = "",
            companions = listOf(getRandomCompanion(), getRandomCompanion()),
            visitDate = getVisitDate(),
            isVIP = true
        )
    ),

    VisitCard(
        client = getRandomClient(),
        visitData = VisitData(
            mainClientPhone = "",
            companions = listOf(getRandomCompanion()),
            visitDate = getVisitDate(),
            observations = observation
        )
    ),

    VisitCard(
        client = getRandomClient(),
        visitData = VisitData(
            mainClientPhone = "",
            companions = listOf(getRandomCompanion()),
            visitDate = getVisitDate(),
        )
    ),
    VisitCard(
        client = getRandomClient(),
        visitData = VisitData(
            mainClientPhone = "",
            companions = listOf(getRandomCompanion(), getRandomCompanion(), getRandomCompanion()),
            visitDate = getVisitDate(),
            observations = observation,
            isVIP = true
        )
    ),

    VisitCard(
        client = getRandomClient(),
        visitData = VisitData(
            mainClientPhone = "",
            visitDate = getVisitDate(),
        )
    ),

    VisitCard(
        client = getRandomClient(),
        visitData = VisitData(
            mainClientPhone = "",
            visitDate = getVisitDate(),
            observations = observation,
            isVIP = true
        )
    ),

    VisitCard(
        client = getRandomClient(),
        visitData = VisitData(
            mainClientPhone = "",
            companions = listOf(getRandomCompanion(), getRandomCompanion()),
            visitDate = getVisitDate(),
        )
    ),

    VisitCard(
        client = getRandomClient(),
        visitData = VisitData(
            mainClientPhone = "",
            companions = listOf(getRandomCompanion()),
            visitDate = getVisitDate(),
            observations = observation,
            isVIP = true
        )
    ),

    VisitCard(
        client = getRandomClient(),
        visitData = VisitData(
            mainClientPhone = "",
            companions = listOf(getRandomCompanion()),
            visitDate = getVisitDate(),
            isVIP = true
        )
    ),


    VisitCard(
        client = getRandomClient(),
        visitData = VisitData(
            mainClientPhone = "",
            companions = listOf(getRandomCompanion(), getRandomCompanion(), getRandomCompanion()),
            visitDate = getVisitDate(),
            observations = observation,
            isVIP = true
        )
    ),

    VisitCard(
        client = getRandomClient(),
        visitData = VisitData(
            mainClientPhone = "",
            visitDate = getVisitDate(),
        )
    ),

    VisitCard(
        client = getRandomClient(),
        visitData = VisitData(
            mainClientPhone = "",
            visitDate = getVisitDate(),
            observations = observation
        )
    ),

    VisitCard(
        client = getRandomClient(),
        visitData = VisitData(
            mainClientPhone = "",
            companions = listOf(getRandomCompanion(), getRandomCompanion()),
            visitDate = getVisitDate(),
            isVIP = true
        )
    ),

    VisitCard(
        client = getRandomClient(),
        visitData = VisitData(
            mainClientPhone = "",
            companions = listOf(getRandomCompanion()),
            visitDate = getVisitDate(),
            observations = observation
        )
    ),

    VisitCard(
        client = getRandomClient(),
        visitData = VisitData(
            mainClientPhone = "",
            companions = listOf(getRandomCompanion()),
            visitDate = getVisitDate(),
        )
    ),
    VisitCard(
        client = getRandomClient(),
        visitData = VisitData(
            mainClientPhone = "",
            companions = listOf(getRandomCompanion(), getRandomCompanion(), getRandomCompanion()),
            visitDate = getVisitDate(),
            observations = observation,
            isVIP = true
        )
    ),

    VisitCard(
        client = getRandomClient(),
        visitData = VisitData(
            mainClientPhone = "",
            visitDate = getVisitDate(),
        )
    ),

    VisitCard(
        client = getRandomClient(),
        visitData = VisitData(
            mainClientPhone = "",
            visitDate = getVisitDate(),
            observations = observation,
            isVIP = true
        )
    ),

    VisitCard(
        client = getRandomClient(),
        visitData = VisitData(
            mainClientPhone = "",
            companions = listOf(getRandomCompanion(), getRandomCompanion()),
            visitDate = getVisitDate(),
        )
    ),

    VisitCard(
        client = getRandomClient(),
        visitData = VisitData(
            mainClientPhone = "",
            companions = listOf(getRandomCompanion()),
            visitDate = getVisitDate(),
            observations = observation,
            isVIP = true
        )
    ),

    VisitCard(
        client = getRandomClient(),
        visitData = VisitData(
            mainClientPhone = "",
            companions = listOf(getRandomCompanion()),
            visitDate = getVisitDate(),
            isVIP = true
        )
    ),

    VisitCard(
        client = getRandomClient(),
        visitData = VisitData(
            mainClientPhone = "",
            companions = listOf(getRandomCompanion(), getRandomCompanion(), getRandomCompanion()),
            visitDate = getVisitDate(),
            observations = observation,
            isVIP = true
        )
    ),

    VisitCard(
        client = getRandomClient(),
        visitData = VisitData(
            mainClientPhone = "",
            visitDate = getVisitDate(),
        )
    ),

    VisitCard(
        client = getRandomClient(),
        visitData = VisitData(
            mainClientPhone = "",
            visitDate = getVisitDate(),
            observations = observation
        )
    ),

    VisitCard(
        client = getRandomClient(),
        visitData = VisitData(
            mainClientPhone = "",
            companions = listOf(getRandomCompanion(), getRandomCompanion()),
            visitDate = getVisitDate(),
            isVIP = true
        )
    ),

    VisitCard(
        client = getRandomClient(),
        visitData = VisitData(
            mainClientPhone = "",
            companions = listOf(getRandomCompanion()),
            visitDate = getVisitDate(),
            observations = observation
        )
    ),

    VisitCard(
        client = getRandomClient(),
        visitData = VisitData(
            mainClientPhone = "",
            companions = listOf(getRandomCompanion()),
            visitDate = getVisitDate(),
        )
    ),
    VisitCard(
        client = getRandomClient(),
        visitData = VisitData(
            mainClientPhone = "",
            companions = listOf(getRandomCompanion(), getRandomCompanion(), getRandomCompanion()),
            visitDate = getVisitDate(),
            observations = observation,
            isVIP = true
        )
    ),

    VisitCard(
        client = getRandomClient(),
        visitData = VisitData(
            mainClientPhone = "",
            visitDate = getVisitDate(),
        )
    ),

    VisitCard(
        client = getRandomClient(),
        visitData = VisitData(
            mainClientPhone = "",
            visitDate = getVisitDate(),
            observations = observation,
            isVIP = true
        )
    ),

    VisitCard(
        client = getRandomClient(),
        visitData = VisitData(
            mainClientPhone = "",
            companions = listOf(getRandomCompanion(), getRandomCompanion()),
            visitDate = getVisitDate(),
        )
    ),

    VisitCard(
        client = getRandomClient(),
        visitData = VisitData(
            mainClientPhone = "",
            companions = listOf(getRandomCompanion()),
            visitDate = getVisitDate(),
            observations = observation,
            isVIP = true
        )
    ),

    VisitCard(
        client = getRandomClient(),
        visitData = VisitData(
            mainClientPhone = "",
            companions = listOf(getRandomCompanion()),
            visitDate = getVisitDate(),
            isVIP = true
        )
    ),
)

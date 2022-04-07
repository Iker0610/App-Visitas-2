package das.omegaterapia.visits.data

import das.omegaterapia.visits.model.entities.Client
import das.omegaterapia.visits.model.entities.Direction


val clients = mutableListOf(

    Client(
        name = "Manolo",
        surname = "Pérez Ramírez",
        phoneNum = "+34655145260",
        direction = Direction(
            address = "Calle Henao, 3, Bajo 3º",
            town = "Bilbao",
            zip = "48009"
        )
    ),

    Client(
        name = "Eider",
        surname = "Mesa Rojo",
        phoneNum = "+34614819500",
        direction = Direction(
            address = "Av. de la Paz, 71 , Ático 2º",
            town = "Logroño",
            zip = "26004"
        )
    ),

    Client(
        name = "Teodora",
        surname = "Teodora  Maroto",
        phoneNum = "+34969003677",
        direction = Direction(
            address = "C. el Arca, 13 , 34º D",
            town = "Lardero",
            zip = "26140"
        )
    ),

    Client(
        name = "Nagore",
        surname = "Idiazabal",
        phoneNum = "+34992581888",
        direction = Direction(
            address = "Ronda Verduzco, 5, 8º 4º",
            town = "Viana",
            zip = "31230"
        )
    ),

    Client(
        name = "Jose",
        surname = "San Martin",
        phoneNum = "+34935368257",
        direction = Direction(
            address = "Vicente Goicoechea Kalea, 4, 44º F",
            town = "Gasteiz",
            zip = "01008"
        )
    ),

    Client(
        name = "Erik",
        surname = "Miguel",
        phoneNum = "+34606146126",
        direction = Direction(
            address = "Joaquin Jose Landazuri Kalea, 17, 6º",
            town = "Gasteiz",
            zip = "01008"
        )
    ),

    Client(
        name = "Anna",
        surname = "Castro",
        phoneNum = "+34642683678",
        direction = Direction(
            address = "C. del Río Ayuda, 16, Bajo 0º",
            town = "Gasteiz",
            zip = "01010"
        )
    ),

    Client(
        name = "Adam",
        surname = "Rosas",
        phoneNum = "+34648372563",
        direction = Direction(
            address = "Urbanizacion los Jardines, 5, Ático 2º",
            town = "Camargo",
            zip = "39600"
        )
    ),

    Client(
        name = "Erik",
        surname = "Miguel",
        phoneNum = "+34606773348",
        direction = Direction(
            address = "C. Larramendi, 6, 3º",
            town = "Tolosa",
            zip = "20400"
        )
    ),
)
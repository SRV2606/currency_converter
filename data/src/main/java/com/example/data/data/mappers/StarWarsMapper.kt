package com.example.data.data.mappers

import com.example.data.data.serverModels.Match
import com.example.data.data.serverModels.Player
import com.example.data.data.serverModels.ServerDecathlonSKUItem
import com.example.domain.domain.models.DecathlonSKUItemBean
import com.example.domain.domain.models.ListSorters
import com.example.domain.domain.models.MatchBean
import com.example.domain.domain.models.PlayerBean
import com.example.domain.models.ClientResult
import javax.inject.Inject

class StarWarsMapper @Inject constructor() {

    fun toPlayer(
        serverResponse: ClientResult<List<Player>>
    ): ClientResult<List<PlayerBean>> {
        return serverResponse.let { clientRes ->
            when (clientRes) {
                is ClientResult.Success -> {
                    val result = clientRes.data.let {
                        it.map { mappedItem ->
                            PlayerBean(
                                id = mappedItem.id,
                                name = mappedItem.name,
                                icon = mappedItem.icon
                            )
                        }
                    }
                    return ClientResult.Success(result)
                }

                is ClientResult.Error -> {
                    return ClientResult.Error(clientRes.error)

                }

                else -> {
                    ClientResult.InProgress
                }
            }
        }
    }

    fun toMatch(
        serverResponse: ClientResult<List<Match>>
    ): ClientResult<List<MatchBean>> {
        return serverResponse.let { clientRes ->
            when (clientRes) {
                is ClientResult.Success -> {
                    val result = clientRes.data.let {
                        it.map { mappedItem ->
                            MatchBean(
                                match = mappedItem.match,
                                player1 = mappedItem.player1.convert(),
                                player2 = mappedItem.player2.convert()
                            )
                        }
                    }
                    return ClientResult.Success(result)
                }

                is ClientResult.Error -> {
                    return ClientResult.Error(clientRes.error)

                }

                else -> {
                    ClientResult.InProgress
                }
            }
        }
    }

    //mapper to convert ServerData to Apps bean model class with requred data
    //[isDummyTrue]  var is for local impl as we dont have an api live yet
    fun toSkuItem(
        isDummyTrue: Boolean,
        serverResponse: ClientResult<List<ServerDecathlonSKUItem>>
    ): ClientResult<List<DecathlonSKUItemBean>> {
        return if (!isDummyTrue)
            serverResponse.let { clientRes ->
                when (clientRes) {
                    is ClientResult.Success -> {
                        val result = clientRes.data.let {
                            it.map { mappedItem ->
                                DecathlonSKUItemBean(
                                    id = mappedItem.id,
                                    name = mappedItem.name,
                                    brand = mappedItem.brand,
                                    imageUrl = mappedItem.imageUrl,
                                    price = mappedItem.price
                                )
                            }
                        }
                        return ClientResult.Success(result)
                    }

                    is ClientResult.Error -> {
                        return ClientResult.Error(clientRes.error)

                    }

                    else -> {
                        ClientResult.InProgress
                    }
                }
            } else {
            val mappedList = toLocalList1().map { mappedItem ->
                DecathlonSKUItemBean(
                    id = mappedItem.id,
                    name = mappedItem.name,
                    brand = mappedItem.brand,
                    imageUrl = mappedItem.imageUrl,
                    price = mappedItem.price
                )
            }

            return ClientResult.Success(mappedList)
        }
    }

    //mapper to coverted filtered List via search to required app bean class
    //[isDummyTrue]  var is for local impl as we dont have an api live yet
    //local logic for coversion is there , as we dont have live api
    fun toFilteredSkuItem(
        isDummyTrue: Boolean,
        searchQuery: String,
        serverResponse: ClientResult<List<ServerDecathlonSKUItem>>
    ): ClientResult<List<DecathlonSKUItemBean>> {
        return if (!isDummyTrue)
            serverResponse.let { clientRes ->
                when (clientRes) {
                    is ClientResult.Success -> {
                        val result = clientRes.data.let {
                            it.map { mappedItem ->
                                DecathlonSKUItemBean(
                                    id = mappedItem.id,
                                    name = mappedItem.name,
                                    brand = mappedItem.brand,
                                    imageUrl = mappedItem.imageUrl,
                                    price = mappedItem.price
                                )
                            }
                        }
                        return ClientResult.Success(result)
                    }

                    is ClientResult.Error -> {
                        return ClientResult.Error(clientRes.error)

                    }

                    else -> {
                        ClientResult.InProgress
                    }
                }
            } else {
            val mappedList = toLocalList1().filter {
                it.name.contains(searchQuery, true) || it.brand.contains(searchQuery, true)
            }.map { mappedItem ->
                DecathlonSKUItemBean(
                    id = mappedItem.id,
                    name = mappedItem.name,
                    brand = mappedItem.brand,
                    imageUrl = mappedItem.imageUrl,
                    price = mappedItem.price
                )
            }

            return ClientResult.Success(mappedList)
        }
    }

    //mapper to coverted sorted list   via chip selection to required app bean class
    //[isDummyTrue]  var is for local impl as we dont have an api live yet
    //local logic for coversion is there , as we dont have live api
    fun toSortedSkuItems(
        isDummyTrue: Boolean,
        sortedBy: ListSorters,
        serverResponse: ClientResult<List<ServerDecathlonSKUItem>>
    ): ClientResult<List<DecathlonSKUItemBean>> {
        return if (!isDummyTrue) {
            when (serverResponse) {
                is ClientResult.Success -> {
                    val result = serverResponse.data
                        .map { mappedItem ->
                            DecathlonSKUItemBean(
                                id = mappedItem.id,
                                name = mappedItem.name,
                                brand = mappedItem.brand,
                                imageUrl = mappedItem.imageUrl,
                                price = mappedItem.price
                            )
                        }
                    ClientResult.Success(result)
                }

                is ClientResult.Error -> {
                    ClientResult.Error(serverResponse.error)
                }

                else -> {
                    ClientResult.InProgress
                }
            }
        } else {
            val mappedList = toLocalList1().map { mappedItem ->
                DecathlonSKUItemBean(
                    id = mappedItem.id,
                    name = mappedItem.name,
                    brand = mappedItem.brand,
                    imageUrl = mappedItem.imageUrl,
                    price = mappedItem.price
                )
            }.sortedWith(
                compareBy {
                    when (sortedBy) {
                        ListSorters.NAME -> it.name
                        ListSorters.PRICE

                        -> it.price

                        ListSorters.BRAND

                        -> it.brand

                        ListSorters.NOTHING

                        -> it.name
                    }
                }
            )
            ClientResult.Success(mappedList)
        }
    }


    //local dummy data
    fun toLocalList() = run {
        return@run listOf(
            ServerDecathlonSKUItem(
                name = "Running Shoes",
                id = "SKU001",
                price = 59.99f,
                imageUrl = "https://example.com/shoes1.jpg",
                brand = "Decathlon"
            ),
            ServerDecathlonSKUItem(
                name = "Cycling Helmet",
                id = "SKU002",
                price = 29.99f,
                imageUrl = "https://example.com/helmet1.jpg",
                brand = "Decathlon"
            ),
            ServerDecathlonSKUItem(
                name = "Tennis Racket",
                id = "SKU003",
                price = 49.99f,
                imageUrl = "https://example.com/racket1.jpg",
                brand = "Decathlon"
            ),
            ServerDecathlonSKUItem(
                name = "Yoga Mat",
                id = "SKU004",
                price = 19.99f,
                imageUrl = "https://example.com/mat1.jpg",
                brand = "Decathlon"
            ),
            ServerDecathlonSKUItem(
                name = "Swimming Goggles",
                id = "SKU005",
                price = 15.99f,
                imageUrl = "https://example.com/goggles1.jpg",
                brand = "Decathlon"
            ),
            ServerDecathlonSKUItem(
                name = "Hiking Backpack",
                id = "SKU006",
                price = 39.99f,
                imageUrl = "https://example.com/backpack1.jpg",
                brand = "Decathlon"
            ),
            ServerDecathlonSKUItem(
                name = "Football",
                id = "SKU007",
                price = 24.99f,
                imageUrl = "https://example.com/football1.jpg",
                brand = "Decathlon"
            ),
            ServerDecathlonSKUItem(
                name = "Gym Gloves",
                id = "SKU008",
                price = 12.99f,
                imageUrl = "https://example.com/gloves1.jpg",
                brand = "Decathlon"
            ),
            ServerDecathlonSKUItem(
                name = "Camping Tent",
                id = "SKU009",
                price = 79.99f,
                imageUrl = "https://example.com/tent1.jpg",
                brand = "Decathlon"
            ),
            ServerDecathlonSKUItem(
                name = "Badminton Set",
                id = "SKU010",
                price = 34.99f,
                imageUrl = "https://example.com/badminton1.jpg",
                brand = "Decathlon"
            ),
            ServerDecathlonSKUItem(
                name = "Running Shoes",
                id = "SKU001",
                price = 59.99f,
                imageUrl = "https://example.com/shoes1.jpg",
                brand = "Decathlon"
            ),
            ServerDecathlonSKUItem(
                name = "Cycling Helmet",
                id = "SKU002",
                price = 29.99f,
                imageUrl = "https://example.com/helmet1.jpg",
                brand = "Decathlon"
            ),
            ServerDecathlonSKUItem(
                name = "Tennis Racket",
                id = "SKU003",
                price = 49.99f,
                imageUrl = "https://example.com/racket1.jpg",
                brand = "Decathlon"
            ),
            ServerDecathlonSKUItem(
                name = "Yoga Mat",
                id = "SKU004",
                price = 19.99f,
                imageUrl = "https://example.com/mat1.jpg",
                brand = "Decathlon"
            ),
            ServerDecathlonSKUItem(
                name = "Swimming Goggles",
                id = "SKU005",
                price = 15.99f,
                imageUrl = "https://example.com/goggles1.jpg",
                brand = "Decathlon"
            ),
            ServerDecathlonSKUItem(
                name = "Hiking Backpack",
                id = "SKU006",
                price = 39.99f,
                imageUrl = "https://example.com/backpack1.jpg",
                brand = "Decathlon"
            ),
            ServerDecathlonSKUItem(
                name = "Football",
                id = "SKU007",
                price = 24.99f,
                imageUrl = "https://example.com/football1.jpg",
                brand = "Decathlon"
            ),
            ServerDecathlonSKUItem(
                name = "Gym Gloves",
                id = "SKU008",
                price = 12.99f,
                imageUrl = "https://example.com/gloves1.jpg",
                brand = "Decathlon"
            ),
            ServerDecathlonSKUItem(
                name = "Camping Tent",
                id = "SKU009",
                price = 79.99f,
                imageUrl = "https://example.com/tent1.jpg",
                brand = "Decathlon"
            ),
            ServerDecathlonSKUItem(
                name = "Badminton Set",
                id = "SKU010",
                price = 34.99f,
                imageUrl = "https://example.com/badminton1.jpg",
                brand = "Decathlon"
            ),
            ServerDecathlonSKUItem(
                name = "Running Shoes",
                id = "SKU001",
                price = 59.99f,
                imageUrl = "https://example.com/shoes1.jpg",
                brand = "Decathlon"
            ),
            ServerDecathlonSKUItem(
                name = "Cycling Helmet",
                id = "SKU002",
                price = 29.99f,
                imageUrl = "https://example.com/helmet1.jpg",
                brand = "Decathlon"
            ),
            ServerDecathlonSKUItem(
                name = "Tennis Racket",
                id = "SKU003",
                price = 49.99f,
                imageUrl = "https://example.com/racket1.jpg",
                brand = "Decathlon"
            ),
            ServerDecathlonSKUItem(
                name = "Yoga Mat",
                id = "SKU004",
                price = 19.99f,
                imageUrl = "https://example.com/mat1.jpg",
                brand = "Decathlon"
            ),
            ServerDecathlonSKUItem(
                name = "Swimming Goggles",
                id = "SKU005",
                price = 15.99f,
                imageUrl = "https://example.com/goggles1.jpg",
                brand = "Decathlon"
            ),
            ServerDecathlonSKUItem(
                name = "Hiking Backpack",
                id = "SKU006",
                price = 39.99f,
                imageUrl = "https://example.com/backpack1.jpg",
                brand = "Decathlon"
            ),
            ServerDecathlonSKUItem(
                name = "Football",
                id = "SKU007",
                price = 24.99f,
                imageUrl = "https://example.com/football1.jpg",
                brand = "Decathlon"
            ),
            ServerDecathlonSKUItem(
                name = "Gym Gloves",
                id = "SKU008",
                price = 12.99f,
                imageUrl = "https://example.com/gloves1.jpg",
                brand = "Decathlon"
            ),
            ServerDecathlonSKUItem(
                name = "Camping Tent",
                id = "SKU009",
                price = 79.99f,
                imageUrl = "https://example.com/tent1.jpg",
                brand = "Decathlon"
            ),
            ServerDecathlonSKUItem(
                name = "Badminton Set",
                id = "SKU010",
                price = 34.99f,
                imageUrl = "https://example.com/badminton1.jpg",
                brand = "Decathlon"
            ),
            ServerDecathlonSKUItem(
                name = "BMX Bike 1",
                id = "BMX001",
                price = 199.99f,
                imageUrl = "https://example.com/bmx_bike1.jpg",
                brand = "Decathlon"
            ),
            ServerDecathlonSKUItem(
                name = "BMX Helmet 1",
                id = "BMX002",
                price = 39.99f,
                imageUrl = "https://example.com/bmx_helmet1.jpg",
                brand = "Decathlon"
            ),
            ServerDecathlonSKUItem(
                name = "BMX Accessories Kit 1",
                id = "BMX003",
                price = 29.99f,
                imageUrl = "https://example.com/bmx_accessories1.jpg",
                brand = "Decathlon"
            ),
            ServerDecathlonSKUItem(
                name = "BMX T-Shirt 1",
                id = "BMX004",
                price = 14.99f,
                imageUrl = "https://example.com/bmx_tshirt1.jpg",
                brand = "Decathlon"
            ),
            ServerDecathlonSKUItem(
                name = "BMX Gloves 1",
                id = "BMX005",
                price = 19.99f,
                imageUrl = "https://example.com/bmx_gloves1.jpg",
                brand = "Decathlon"
            ),
            // Ranger items
            ServerDecathlonSKUItem(
                name = "Ranger Bike 1",
                id = "RANGER001",
                price = 249.99f,
                imageUrl = "https://example.com/ranger_bike1.jpg",
                brand = "Decathlon"
            ),
            ServerDecathlonSKUItem(
                name = "Ranger Helmet 1",
                id = "RANGER002",
                price = 49.99f,
                imageUrl = "https://example.com/ranger_helmet1.jpg",
                brand = "Decathlon"
            ),
            ServerDecathlonSKUItem(
                name = "Ranger Accessories Kit 1",
                id = "RANGER003",
                price = 39.99f,
                imageUrl = "https://example.com/ranger_accessories1.jpg",
                brand = "Decathlon"
            ),
            ServerDecathlonSKUItem(
                name = "Ranger T-Shirt 1",
                id = "RANGER004",
                price = 19.99f,
                imageUrl = "https://example.com/ranger_tshirt1.jpg",
                brand = "Decathlon"
            ),
            ServerDecathlonSKUItem(
                name = "Ranger Gloves 1",
                id = "RANGER005",
                price = 24.99f,
                imageUrl = "https://example.com/ranger_gloves1.jpg",
                brand = "Decathlon"
            ),
            // Other items
            ServerDecathlonSKUItem(
                name = "Running Shoes",
                id = "SKU001",
                price = 59.99f,
                imageUrl = "https://example.com/shoes1.jpg",
                brand = "Decathlon"
            ),
            ServerDecathlonSKUItem(
                name = "Cycling Helmet",
                id = "SKU002",
                price = 29.99f,
                imageUrl = "https://example.com/helmet1.jpg",
                brand = "Decathlon"
            ),
            ServerDecathlonSKUItem(
                name = "Tennis Racket",
                id = "SKU003",
                price = 49.99f,
                imageUrl = "https://example.com/racket1.jpg",
                brand = "Decathlon"
            ),
            ServerDecathlonSKUItem(
                name = "Yoga Mat",
                id = "SKU004",
                price = 19.99f,
                imageUrl = "https://example.com/mat1.jpg",
                brand = "Decathlon"
            ),
            ServerDecathlonSKUItem(
                name = "Swimming Goggles",
                id = "SKU005",
                price = 15.99f,
                imageUrl = "https://example.com/goggles1.jpg",
                brand = "Decathlon"
            ),
            ServerDecathlonSKUItem(
                name = "Hiking Backpack",
                id = "SKU006",
                price = 39.99f,
                imageUrl = "https://example.com/backpack1.jpg",
                brand = "Decathlon"
            ),
            ServerDecathlonSKUItem(
                name = "Football",
                id = "SKU007",
                price = 24.99f,
                imageUrl = "https://example.com/football1.jpg",
                brand = "Decathlon"
            ),
            ServerDecathlonSKUItem(
                name = "Gym Gloves",
                id = "SKU008",
                price = 12.99f,
                imageUrl = "https://example.com/gloves1.jpg",
                brand = "Decathlon"
            ),
            ServerDecathlonSKUItem(
                name = "Camping Tent",
                id = "SKU009",
                price = 79.99f,
                imageUrl = "https://example.com/tent1.jpg",
                brand = "Decathlon"
            ),
            ServerDecathlonSKUItem(
                name = "Badminton Set",
                id = "SKU010",
                price = 34.99f,
                imageUrl = "https://example.com/badminton1.jpg",
                brand = "Decathlon"
            ),
            ServerDecathlonSKUItem(
                name = "BMX Bike 1",
                id = "BMX001",
                price = 199.99f,
                imageUrl = "https://example.com/bmx_bike1.jpg",
                brand = "Decathlon"
            ),
            ServerDecathlonSKUItem(
                name = "BMX Helmet 1",
                id = "BMX002",
                price = 39.99f,
                imageUrl = "https://example.com/bmx_helmet1.jpg",
                brand = "Decathlon"
            ),
            ServerDecathlonSKUItem(
                name = "BMX Accessories Kit 1",
                id = "BMX003",
                price = 29.99f,
                imageUrl = "https://example.com/bmx_accessories1.jpg",
                brand = "Decathlon"
            ),
            ServerDecathlonSKUItem(
                name = "BMX T-Shirt 1",
                id = "BMX004",
                price = 14.99f,
                imageUrl = "https://example.com/bmx_tshirt1.jpg",
                brand = "Decathlon"
            ),
            ServerDecathlonSKUItem(
                name = "BMX Gloves 1",
                id = "BMX005",
                price = 19.99f,
                imageUrl = "https://example.com/bmx_gloves1.jpg",
                brand = "Decathlon"
            ),
            // Ranger items
            ServerDecathlonSKUItem(
                name = "Ranger Bike 1",
                id = "RANGER001",
                price = 249.99f,
                imageUrl = "https://example.com/ranger_bike1.jpg",
                brand = "Decathlon"
            ),
            ServerDecathlonSKUItem(
                name = "Ranger Helmet 1",
                id = "RANGER002",
                price = 49.99f,
                imageUrl = "https://example.com/ranger_helmet1.jpg",
                brand = "Decathlon"
            ),
            ServerDecathlonSKUItem(
                name = "Ranger Accessories Kit 1",
                id = "RANGER003",
                price = 39.99f,
                imageUrl = "https://example.com/ranger_accessories1.jpg",
                brand = "Decathlon"
            ),
            ServerDecathlonSKUItem(
                name = "Ranger T-Shirt 1",
                id = "RANGER004",
                price = 19.99f,
                imageUrl = "https://example.com/ranger_tshirt1.jpg",
                brand = "Decathlon"
            ),
            ServerDecathlonSKUItem(
                name = "Ranger Gloves 1",
                id = "RANGER005",
                price = 24.99f,
                imageUrl = "https://example.com/ranger_gloves1.jpg",
                brand = "Decathlon"
            ),
            // Other items
            ServerDecathlonSKUItem(
                name = "Running Shoes",
                id = "SKU001",
                price = 59.99f,
                imageUrl = "https://example.com/shoes1.jpg",
                brand = "Decathlon"
            ),
            ServerDecathlonSKUItem(
                name = "Cycling Helmet",
                id = "SKU002",
                price = 29.99f,
                imageUrl = "https://example.com/helmet1.jpg",
                brand = "Decathlon"
            ),
            ServerDecathlonSKUItem(
                name = "Tennis Racket",
                id = "SKU003",
                price = 49.99f,
                imageUrl = "https://example.com/racket1.jpg",
                brand = "Decathlon"
            ),
            ServerDecathlonSKUItem(
                name = "Yoga Mat",
                id = "SKU004",
                price = 19.99f,
                imageUrl = "https://example.com/mat1.jpg",
                brand = "Decathlon"
            ),
            ServerDecathlonSKUItem(
                name = "Swimming Goggles",
                id = "SKU005",
                price = 15.99f,
                imageUrl = "https://example.com/goggles1.jpg",
                brand = "Decathlon"
            ),
            ServerDecathlonSKUItem(
                name = "Hiking Backpack",
                id = "SKU006",
                price = 39.99f,
                imageUrl = "https://example.com/backpack1.jpg",
                brand = "Decathlon"
            ),
            ServerDecathlonSKUItem(
                name = "Football",
                id = "SKU007",
                price = 24.99f,
                imageUrl = "https://example.com/football1.jpg",
                brand = "Decathlon"
            ),
            ServerDecathlonSKUItem(
                name = "Gym Gloves",
                id = "SKU008",
                price = 12.99f,
                imageUrl = "https://example.com/gloves1.jpg",
                brand = "Decathlon"
            ),
            ServerDecathlonSKUItem(
                name = "Camping Tent",
                id = "SKU009",
                price = 79.99f,
                imageUrl = "https://example.com/tent1.jpg",
                brand = "Decathlon"
            ),
            ServerDecathlonSKUItem(
                name = "Badminton Set",
                id = "SKU010",
                price = 34.99f,
                imageUrl = "https://example.com/badminton1.jpg",
                brand = "Decathlon"
            )
        )
    }
}

private fun Match.MatchPlayer.convert(): MatchBean.MatchPlayerBean {
    return MatchBean.MatchPlayerBean(id = id, score = score)
}


//local dummy data
fun toLocalList1(): List<ServerDecathlonSKUItem> {
    return listOf(
        // Common items
        ServerDecathlonSKUItem(
            name = "Running Shoes",
            id = "SKU001",
            price = 59.99f,
            imageUrl = "https://example.com/shoes1.jpg",
            brand = "Decathlon"
        ),
        ServerDecathlonSKUItem(
            name = "Cycling Helmet",
            id = "SKU002",
            price = 29.99f,
            imageUrl = "https://example.com/helmet1.jpg",
            brand = "Decathlon"
        ),
        ServerDecathlonSKUItem(
            name = "Tennis Racket",
            id = "SKU003",
            price = 49.99f,
            imageUrl = "https://example.com/racket1.jpg",
            brand = "Decathlon"
        ),
        ServerDecathlonSKUItem(
            name = "Yoga Mat",
            id = "SKU004",
            price = 19.99f,
            imageUrl = "https://example.com/mat1.jpg",
            brand = "Decathlon"
        ),
        ServerDecathlonSKUItem(
            name = "Swimming Goggles",
            id = "SKU005",
            price = 15.99f,
            imageUrl = "https://example.com/goggles1.jpg",
            brand = "Decathlon"
        ),
        ServerDecathlonSKUItem(
            name = "Hiking Backpack",
            id = "SKU006",
            price = 39.99f,
            imageUrl = "https://example.com/backpack1.jpg",
            brand = "Decathlon"
        ),
        ServerDecathlonSKUItem(
            name = "Football",
            id = "SKU007",
            price = 24.99f,
            imageUrl = "https://example.com/football1.jpg",
            brand = "Decathlon"
        ),
        ServerDecathlonSKUItem(
            name = "Gym Gloves",
            id = "SKU008",
            price = 12.99f,
            imageUrl = "https://example.com/gloves1.jpg",
            brand = "Decathlon"
        ),
        ServerDecathlonSKUItem(
            name = "Camping Tent",
            id = "SKU009",
            price = 79.99f,
            imageUrl = "https://example.com/tent1.jpg",
            brand = "Decathlon"
        ),
        ServerDecathlonSKUItem(
            name = "Badminton Set",
            id = "SKU010",
            price = 34.99f,
            imageUrl = "https://example.com/badminton1.jpg",
            brand = "Decathlon"
        ),

        // BMX items
        ServerDecathlonSKUItem(
            name = "BMX Bike 1",
            id = "BMX001",
            price = 199.99f,
            imageUrl = "https://example.com/bmx_bike1.jpg",
            brand = "Decathlon"
        ),
        ServerDecathlonSKUItem(
            name = "BMX Helmet 1",
            id = "BMX002",
            price = 39.99f,
            imageUrl = "https://example.com/bmx_helmet1.jpg",
            brand = "Decathlon"
        ),
        ServerDecathlonSKUItem(
            name = "BMX Accessories Kit 1",
            id = "BMX003",
            price = 29.99f,
            imageUrl = "https://example.com/bmx_accessories1.jpg",
            brand = "Decathlon"
        ),
        ServerDecathlonSKUItem(
            name = "BMX T-Shirt 1",
            id = "BMX004",
            price = 14.99f,
            imageUrl = "https://example.com/bmx_tshirt1.jpg",
            brand = "Decathlon"
        ),
        ServerDecathlonSKUItem(
            name = "BMX Gloves 1",
            id = "BMX005",
            price = 19.99f,
            imageUrl = "https://example.com/bmx_gloves1.jpg",
            brand = "Decathlon"
        ),

        // Ranger items
        ServerDecathlonSKUItem(
            name = "Ranger Bike 1",
            id = "RANGER001",
            price = 249.99f,
            imageUrl = "https://example.com/ranger_bike1.jpg",
            brand = "Decathlon"
        ),
        ServerDecathlonSKUItem(
            name = "Ranger Helmet 1",
            id = "RANGER002",
            price = 49.99f,
            imageUrl = "https://example.com/ranger_helmet1.jpg",
            brand = "Decathlon"
        ),
        ServerDecathlonSKUItem(
            name = "Ranger Accessories Kit 1",
            id = "RANGER003",
            price = 39.99f,
            imageUrl = "https://example.com/ranger_accessories1.jpg",
            brand = "Decathlon"
        ),
        ServerDecathlonSKUItem(
            name = "Ranger T-Shirt 1",
            id = "RANGER004",
            price = 19.99f,
            imageUrl = "https://example.com/ranger_tshirt1.jpg",
            brand = "Decathlon"
        ),
        ServerDecathlonSKUItem(
            name = "Ranger Gloves 1",
            id = "RANGER005",
            price = 24.99f,
            imageUrl = "https://example.com/ranger_gloves1.jpg",
            brand = "Decathlon"
        ),

        // Additional items
        ServerDecathlonSKUItem(
            name = "Basketball",
            id = "SKU051",
            price = 29.99f,
            imageUrl = "https://example.com/basketball.jpg",
            brand = "Nike"
        ),
        ServerDecathlonSKUItem(
            name = "Golf Clubs",
            id = "SKU052",
            price = 349.99f,
            imageUrl = "https://example.com/golf_clubs.jpg",
            brand = "Callaway"
        ),
        ServerDecathlonSKUItem(
            name = "Running Jacket",
            id = "SKU053",
            price = 49.99f,
            imageUrl = "https://example.com/running_jacket.jpg",
            brand = "Adidas"
        ),
        ServerDecathlonSKUItem(
            name = "Running Shoes",
            id = "SKU001",
            price = 59.99f,
            imageUrl = "https://example.com/shoes1.jpg",
            brand = "Decathlon"
        ),
        ServerDecathlonSKUItem(
            name = "Cycling Helmet",
            id = "SKU002",
            price = 29.99f,
            imageUrl = "https://example.com/helmet1.jpg",
            brand = "Decathlon"
        ),
        ServerDecathlonSKUItem(
            name = "Tennis Racket",
            id = "SKU003",
            price = 49.99f,
            imageUrl = "https://example.com/racket1.jpg",
            brand = "Decathlon"
        ),
        ServerDecathlonSKUItem(
            name = "Yoga Mat",
            id = "SKU004",
            price = 19.99f,
            imageUrl = "https://example.com/mat1.jpg",
            brand = "Decathlon"
        ),
        ServerDecathlonSKUItem(
            name = "Swimming Goggles",
            id = "SKU005",
            price = 15.99f,
            imageUrl = "https://example.com/goggles1.jpg",
            brand = "Decathlon"
        ),
        ServerDecathlonSKUItem(
            name = "Hiking Backpack",
            id = "SKU006",
            price = 39.99f,
            imageUrl = "https://example.com/backpack1.jpg",
            brand = "Decathlon"
        ),
        ServerDecathlonSKUItem(
            name = "Football",
            id = "SKU007",
            price = 24.99f,
            imageUrl = "https://example.com/football1.jpg",
            brand = "Decathlon"
        ),
        ServerDecathlonSKUItem(
            name = "Gym Gloves",
            id = "SKU008",
            price = 12.99f,
            imageUrl = "https://example.com/gloves1.jpg",
            brand = "Decathlon"
        ),
        ServerDecathlonSKUItem(
            name = "Camping Tent",
            id = "SKU009",
            price = 79.99f,
            imageUrl = "https://example.com/tent1.jpg",
            brand = "Decathlon"
        ),
        ServerDecathlonSKUItem(
            name = "Badminton Set",
            id = "SKU010",
            price = 34.99f,
            imageUrl = "https://example.com/badminton1.jpg",
            brand = "Decathlon"
        ),

        // BMX items
        ServerDecathlonSKUItem(
            name = "BMX Bike 1",
            id = "BMX001",
            price = 199.99f,
            imageUrl = "https://example.com/bmx_bike1.jpg",
            brand = "Decathlon"
        ),
        ServerDecathlonSKUItem(
            name = "BMX Helmet 1",
            id = "BMX002",
            price = 39.99f,
            imageUrl = "https://example.com/bmx_helmet1.jpg",
            brand = "Decathlon"
        ),
        ServerDecathlonSKUItem(
            name = "BMX Accessories Kit 1",
            id = "BMX003",
            price = 29.99f,
            imageUrl = "https://example.com/bmx_accessories1.jpg",
            brand = "Decathlon"
        ),
        ServerDecathlonSKUItem(
            name = "BMX T-Shirt 1",
            id = "BMX004",
            price = 14.99f,
            imageUrl = "https://example.com/bmx_tshirt1.jpg",
            brand = "Decathlon"
        ),
        ServerDecathlonSKUItem(
            name = "BMX Gloves 1",
            id = "BMX005",
            price = 19.99f,
            imageUrl = "https://example.com/bmx_gloves1.jpg",
            brand = "Decathlon"
        ),

        // Ranger items
        ServerDecathlonSKUItem(
            name = "Ranger Bike 1",
            id = "RANGER001",
            price = 249.99f,
            imageUrl = "https://example.com/ranger_bike1.jpg",
            brand = "Decathlon"
        ),
        ServerDecathlonSKUItem(
            name = "Ranger Helmet 1",
            id = "RANGER002",
            price = 49.99f,
            imageUrl = "https://example.com/ranger_helmet1.jpg",
            brand = "Decathlon"
        ),
        ServerDecathlonSKUItem(
            name = "Ranger Accessories Kit 1",
            id = "RANGER003",
            price = 39.99f,
            imageUrl = "https://example.com/ranger_accessories1.jpg",
            brand = "Decathlon"
        ),
        ServerDecathlonSKUItem(
            name = "Ranger T-Shirt 1",
            id = "RANGER004",
            price = 19.99f,
            imageUrl = "https://example.com/ranger_tshirt1.jpg",
            brand = "Decathlon"
        ),
        ServerDecathlonSKUItem(
            name = "Ranger Gloves 1",
            id = "RANGER005",
            price = 24.99f,
            imageUrl = "https://example.com/ranger_gloves1.jpg",
            brand = "Decathlon"
        ),

        // Additional items
        ServerDecathlonSKUItem(
            name = "Basketball",
            id = "SKU051",
            price = 29.99f,
            imageUrl = "https://example.com/basketball.jpg",
            brand = "Nike"
        ),
        ServerDecathlonSKUItem(
            name = "Golf Clubs",
            id = "SKU052",
            price = 349.99f,
            imageUrl = "https://example.com/golf_clubs.jpg",
            brand = "Callaway"
        ),
        ServerDecathlonSKUItem(
            name = "Running Jacket",
            id = "SKU053",
            price = 49.99f,
            imageUrl = "https://example.com/running_jacket.jpg",
            brand = "Adidas"
        ),
    )


}









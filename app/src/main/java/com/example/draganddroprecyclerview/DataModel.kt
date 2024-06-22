package com.example.draganddroprecyclerview

data class DataModel(val orderId: String, val address: String,var isDisable:Boolean)

fun generateRandomOrderId(): String {
    val randomNumber = (1000000..9999999).random()
    return "A$randomNumber"
}

val dataList: MutableList<DataModel> = mutableListOf(
    DataModel(generateRandomOrderId(), "台北市中正區忠孝西路一段120號", false),
    DataModel(generateRandomOrderId(), "台北市信義區松仁路11號", false),
    DataModel(generateRandomOrderId(), "台北市大安區仁愛路三段53號", true),
    DataModel(generateRandomOrderId(), "台北市士林區文林路762號", false),
    DataModel(generateRandomOrderId(), "台北市內湖區民權東路六段280號", false),
    DataModel(generateRandomOrderId(), "新北市板橋區中山路一段5號", false),
    DataModel(generateRandomOrderId(), "新北市三重區重新路四段110號", true),
    DataModel(generateRandomOrderId(), "新北市中和區中山路二段123號", true),
    DataModel(generateRandomOrderId(), "新北市永和區永和路二段150號", true),
    DataModel(generateRandomOrderId(), "新北市新莊區中正路499號", false),
    DataModel(generateRandomOrderId(), "桃園市中壢區中山東路三段200號", true),
    DataModel(generateRandomOrderId(), "桃園市龜山區文化一路302號", false),
    DataModel(generateRandomOrderId(), "桃園市蘆竹區南崁路一段321號", false),
    DataModel(generateRandomOrderId(), "台中市西屯區台灣大道四段900號", false),
    DataModel(generateRandomOrderId(), "台中市南屯區文心南五路108號", true),
    DataModel(generateRandomOrderId(), "台中市北區育才街85號", false),
    DataModel(generateRandomOrderId(), "台中市東區自由路三段168號", false),
    DataModel(generateRandomOrderId(), "台南市中西區民權路二段135號", true),
    DataModel(generateRandomOrderId(), "台南市東區東門路三段290號", true),
    DataModel(generateRandomOrderId(), "高雄市前鎮區中山二路200號", true),
    DataModel(generateRandomOrderId(), "高雄市三民區民族一路130號", true),
    DataModel(generateRandomOrderId(), "高雄市苓雅區四維四路260號", true),
    DataModel(generateRandomOrderId(), "高雄市左營區博愛三路15號", false),
    DataModel(generateRandomOrderId(), "基隆市信義區信一路345號", true),
    DataModel(generateRandomOrderId(), "新竹市北區中華路二段450號", true),
    DataModel(generateRandomOrderId(), "新竹市東區東門街120號", true),
    DataModel(generateRandomOrderId(), "嘉義市西區文化路300號", true),
    DataModel(generateRandomOrderId(), "嘉義市東區共和路123號", true),
    DataModel(generateRandomOrderId(), "彰化市中正路四段20號", false),
    DataModel(generateRandomOrderId(), "彰化市北區民生路二段115號", true)
)


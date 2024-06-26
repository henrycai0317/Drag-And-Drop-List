package com.example.draganddroprecyclerview

data class DataModel(
    val orderId: String,
    val address: String,
    var isDisable: Boolean,
    var translationX: Float = 0f  // 新增滑動狀態
)

fun generateRandomOrderId(): String {
    val randomNumber = (1000000..9999999).random()
    return "A$randomNumber"
}

val dataList: MutableList<DataModel> = mutableListOf(
    DataModel(generateRandomOrderId(), "No. 120, Section 1, Zhongxiao West Road, Zhongzheng District, Taipei City", false),
    DataModel(generateRandomOrderId(), "No. 11, Songren Road, Xinyi District, Taipei City", false),
    DataModel(generateRandomOrderId(), "No. 53, Section 3, Renai Road, Da'an District, Taipei City", true),
    DataModel(generateRandomOrderId(), "No. 762, Wenlin Road, Shilin District, Taipei City", false),
    DataModel(generateRandomOrderId(), "No. 280, Section 6, Minquan East Road, Neihu District, Taipei City", false),
    DataModel(generateRandomOrderId(), "No. 5, Section 1, Zhongshan Road, Banqiao District, New Taipei City", false),
    DataModel(generateRandomOrderId(), "No. 110, Section 4, Chongxin Road, Sanchong District, New Taipei City", true),
    DataModel(generateRandomOrderId(), "No. 123, Section 2, Zhongshan Road, Zhonghe District, New Taipei City", true),
    DataModel(generateRandomOrderId(), "No. 150, Section 2, Yonghe Road, Yonghe District, New Taipei City", true),
    DataModel(generateRandomOrderId(), "No. 499, Zhongzheng Road, Xinzhuang District, New Taipei City", false),
    DataModel(generateRandomOrderId(), "No. 200, Section 3, Zhongshan East Road, Zhongli District, Taoyuan City", true),
    DataModel(generateRandomOrderId(), "No. 302, Wenhua 1st Road, Guishan District, Taoyuan City", false),
    DataModel(generateRandomOrderId(), "No. 321, Section 1, Nankan Road, Luzhu District, Taoyuan City", false),
    DataModel(generateRandomOrderId(), "No. 900, Section 4, Taiwan Boulevard, Xitun District, Taichung City", false),
    DataModel(generateRandomOrderId(), "No. 108, Wenxin South 5th Road, Nantun District, Taichung City", true),
    DataModel(generateRandomOrderId(), "No. 85, Yucai Street, North District, Taichung City", false),
    DataModel(generateRandomOrderId(), "No. 168, Section 3, Ziyou Road, East District, Taichung City", false),
    DataModel(generateRandomOrderId(), "No. 135, Section 2, Minquan Road, West Central District, Tainan City", true),
    DataModel(generateRandomOrderId(), "No. 290, Section 3, Dongmen Road, East District, Tainan City", true),
    DataModel(generateRandomOrderId(), "No. 200, Zhongshan 2nd Road, Qianzhen District, Kaohsiung City", true),
    DataModel(generateRandomOrderId(), "No. 130, Minzu 1st Road, Sanmin District, Kaohsiung City", true),
    DataModel(generateRandomOrderId(), "No. 260, Siwei 4th Road, Lingya District, Kaohsiung City", true),
    DataModel(generateRandomOrderId(), "No. 15, Bo'ai 3rd Road, Zuoying District, Kaohsiung City", false),
    DataModel(generateRandomOrderId(), "No. 345, Xinyi 1st Road, Xinyi District, Keelung City", true),
    DataModel(generateRandomOrderId(), "No. 450, Section 2, Zhonghua Road, North District, Hsinchu City", true),
    DataModel(generateRandomOrderId(), "No. 120, Dongmen Street, East District, Hsinchu City", true),
    DataModel(generateRandomOrderId(), "No. 300, Wenhua Road, West District, Chiayi City", true),
    DataModel(generateRandomOrderId(), "No. 123, Gonghe Road, East District, Chiayi City", true),
    DataModel(generateRandomOrderId(), "No. 20, Section 4, Zhongzheng Road, Changhua City", false),
    DataModel(generateRandomOrderId(), "No. 115, Section 2, Minsheng Road, North District, Changhua City", true)
)


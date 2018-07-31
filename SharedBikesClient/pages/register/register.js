// pages/register/register.js
Page({

  /**
   * 页面的初始数据
   */
  data: {
    countryCodes: ["86", "80", "84", "87"],
    countryCodeIndex: 0,
    phoneNum: ""
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {

  },

  /**
   * 生命周期函数--监听页面初次渲染完成
   */
  onReady: function () {

  },

  /**
   * 生命周期函数--监听页面显示
   */
  onShow: function () {

  },

  /**
   * 生命周期函数--监听页面隐藏
   */
  onHide: function () {

  },

  /**
   * 生命周期函数--监听页面卸载
   */
  onUnload: function () {

  },

  /**
   * 页面相关事件处理函数--监听用户下拉动作
   */
  onPullDownRefresh: function () {

  },

  /**
   * 页面上拉触底事件的处理函数
   */
  onReachBottom: function () {

  },

  /**
   * 用户点击右上角分享
   */
  onShareAppMessage: function () {

  },

  bindCountryCodeChange: function (e) {
    //console.log('picker country code 发生选择改变，携带值为', e.detail.value);
    this.setData({
      countryCodeIndex: e.detail.value
    })
  },

  inputPhoneNum: function (e) {
    console.log(e)
    this.setData({
      phoneNum: e.detail.value
    })
  },

  genVerifyCode: function () {
    //获取国家代码的索引
    var index = this.data.countryCodeIndex;
    //根据索引取值
    var countryCode = this.data.countryCodes[index];
    //获取输入的手机号
    var phoneNum = this.data.phoneNum;

    console.log(phoneNum)

    //向后台发送请求
    wx.request({
      // 小程序访问的网络请求协议必须是https，url里面不能有端口号
      url: "http://localhost:8080/user/genCode",
      //传递的参数
      data: {
        'countryCode': countryCode,
        'phoneNum': phoneNum
      },
      //发送请求的方式
      method: 'GET',
      //成功的回调函数
      success: function (res) {
        
        wx.showToast({
          title: '验证码已发送',
          icon: 'success',
          duration: 2000
        })
      }
    })
  },

  formSubmit: function (e) {

    //通过这个函数的参数，拿到用户输入的手机号以及验证码
    var phoneNum = e.detail.value.phoneNum
    var verifyCode = e.detail.value.verifyCode

    // 发送手机号和验证码到服务器进行校验
    wx.request({
      url: "http://localhost:8080/user/verify",

      // POST的请求头m默认是json，所以要修改请求头类型
      header: { 'content-type': 'application/x-www-form-urlencoded' },
      data: {
        phoneNum: phoneNum,
        verifyCode: verifyCode
      },
      method: "POST",
      success: function (res) {
        // 回调函数，如果用户输入的验证码和实际的发送的验证码是一致的，那么校验成功，那么就将手机信息保存到mongo中
        if(res.data) {
          wx.request({
            // 微信小程序成产环境请求的协议必须是https，地址必须是域名，不能带端口号
            url: "http://localhost:8080/user/register",
            // 这里不用改变请求头了，因为参数是一个user对象，直接加上注解@RequestBody 提示将以json的形式接受参数
                  //header: { 'content-type': 'application/x-www-form-urlencoded' },
            method: 'POST',
            // data: e.detail.value,
            // method: 'POST',
            // success: function (res) {
            //   // 将手机号写入到手机的磁盘中
            //   wx.setStorageSync("phoneNum", phoneNum)
            //   // 将手机号保存到到内存
            //   var globalData = getApp().globalData
            //   globalData.phoneNum = phoneNum
            //   globalData.status = "deposit"
            //   // 手机信息保存到mongo后，然后跳转到交押金页面
            //   wx.navigateTo({
            //     url: '../deposit/deposit'
            //   })
            // }
            data: {
              phoneNum: phoneNum,
              regDate: new Date(),
              status: 1,
            },
            success: function(res) {
              if (res.data) {
                // 将手机号以及用户状态（1表示已经完成手机验证，2代表已经实名认证）保存到到内存
                var globalData = getApp().globalData;
                globalData.phoneNum = phoneNum;
                globalData.status = 1;

                // // 将手机号写入到手机的存储中
                wx.setStorageSync("phoneNum", phoneNum);
                wx.setStorageSync("status", 1);

                // 跳转到充值页面
                wx.navigateTo({
                  url: '../deposit/deposit',
                })

              } else {
                wx.showModal({
                  title: '提示',
                  content: '服务端错误，请稍后再试',
                })
              }
            }
            
          })
        }
        
        //若校验失败，弹出对话框，提示输入错误，重新输入 
        else {
          wx.showModal({
            title: '提示',
            content: '验证码有误，请重新输入！',
            //不让出现取消的选项，只出现，确定
            showCancel: false
          })
        }
      }
    })
  }
})
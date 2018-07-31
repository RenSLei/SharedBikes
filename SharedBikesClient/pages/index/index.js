//导包
var myUtils=require("../../utils/myUtils.js")

// js主要控制逻辑脚本，目前是Page及里面的onload
Page({
  data: {
    log:0.0 ,
    lat:0.0,
    controls:[],
    markers:[]
  },
   
//  这是首次加载前的调用函数
  onLoad: function () {

    // 获取该页面
    var that = this

    //获取用户的设置信息
    wx.getSetting({
      success(res) {
        //如果用户的res.authSetting['scope.userLocation']为true代表允许小程序访问地理位置，不再弹出访问地理位置的框
        if (res.authSetting['scope.userLocation']){
          wx.getLocation({
            success: function (res) {
              // 获取当前设备的位置信息
              var log = res.longitude
              var lat = res.latitude
              that.setData({
                // 将获取到的经纬度值分别赋给当前页面的data属性中的log和lat
                log: log,
                lat: lat
              })

              //查找单车
              findBikes(log,lat,that)
            },
          })
         
        }
      //如果为false，则弹出对话框向用户请求小程序获取地理位置的权限
       if (!res.authSetting['scope.userLocation']) {
         //请求获取地理位置权限
          wx.authorize({
            scope: 'scope.userLocation',
            success() {
              // 用户已经同意小程序获取地理位置，后续调用 wx.getLocation 接口不会弹窗询问
              wx.getLocation({
                  success: function (res) {
                    // 获取当前设备的位置信息
                    var log = res.longitude
                    var lat = res.latitude
                    that.setData({
                      // 将获取到的经纬度值分别赋给当前页面的data属性中的log和lat
                      log: log,
                      lat: lat
                    })
                    //查找单车
                    findBikes(log, lat, that)
                  },
                })
            }
          })
        }
      }
    })

 
    // 获取当前设备的地理位置，wx是内置对象
    // wx.getLocation({
    //   success: function(res) {
    //     // 获取当前设备的位置信息
    //     var log = res.longitude
    //     var lat = res.latitude
    //     that.setData({
    //       // 将获取到的经纬度值分别赋给当前页面的data属性中的log和lat
    //       log:log,
    //       lat:lat
    //     })
  
    //   },
    // })

    // wx.getSetting({
    //   success: (res) => {

    //     console.log(res.authSetting['scope.userLocation']);
    //     //  res.scope.userLocation = true;

    //   }
    // })


    //获取设备信息，为了使控件位置在不同设备保持同样的相对位置
    wx.getSystemInfo({
      success: function(res) {
        var windowWidth=res.windowWidth;
        var windowHeight=res.windowHeight;
        
        //  添加控件
        that.setData({
          controls: [
            {
              //扫码按钮
              id: 1,
              iconPath: '/images/qrcode.png',
              position: {
                width:100,
                height: 40,
                left: windowWidth/2 - 50,
                top: windowHeight -60
              },
              clickable: true
            },
            {
              //充值按钮
              id: 2,
              iconPath: '/images/pay.png',
              position: {
                width: 25,
                height: 25,
                left: windowWidth - 30,
                top: windowHeight - 80
              },
              clickable: true
            },
            {
              //报修按钮
              id: 3,
              iconPath: '/images/warn.png',
              position: {
                width: 35,
                height: 35,
                left: windowWidth - 35,
                top: windowHeight - 50
              },
              clickable: true
            },
            {
              //定位按钮
              id: 4,
              iconPath: '/images/getloc.png',
              position: {
                width: 40,
                height: 40,
                left: 10,
                top: windowHeight - 60
              },
              clickable: true
            },
            {
              //中心指针按钮
              id: 5,
              iconPath: '/images/location.png',
              position: {
                width: 20,
                height: 35,
                left: windowWidth /2 - 10 ,
                top: windowHeight/2 - 35
              },
              clickable: true
            },
            {
              //添加车辆
              id:6,
              iconPath:'/images/add.png',
              position:{
                width: 30,
                height: 30,
              },
              clickable:true
            },
          ]

        })
      },

    })

  },

    // 控件被点击的事件
    controltap : function (e){
      var that= this;
      var cid=e.controlId;

      switch(cid)//对点击事件的id进行捕捉，捕捉到对应的id就执行相应的事件
      {
        //点击扫码按钮
        case 1: {
          
          //使用工具类中的方法来获取相应的结果
          var status=myUtils.get("status");
          //如果是0跳转到手机注册页面
          if(status==0){
            wx.navigateTo({
              url: '../register/register',
            })
          }
          //如果是1则说明已经完成了手机验证的功能，直接跳转到交押金的页面
          else if(status==1){
            wx.navigateTo({
              url: '../deposit/deposit',
            })
          }
          else if(status==2){
            wx.navigateTo({
              url: '../identify/identify',
            })
          }

          break;
        }

        //回到当前的位置
        case 4:{
          this.mapCtx.moveToLocation();//mapCtx记住了页面初始的信息
          break;
        }
        case 6:{
          //添加车辆
          //获取当前已有的车辆
          var bikes=that.data.markers;
          this.mapCtx.getCenterLocation({
            success: function (res) {
              //用变量来获取当前中心点的位置
              var tlog=res.longitude;
              var tlat=res.latitude;

              // bikes.push(
              //   {
              //     iconPath: "/images/bike.png",
              //     width: 30,
              //     height: 35,
              //     // 使用每次移动地图后的那个经纬度临时信息对添加的单车的位置
              //     longitude:tlog,
              //     latitude:tlat
              //   }
              // )
              // //重新将新的marks设置到data中
              // that.setData({
              //   markers: bikes
              // }) 

              //将以上在地图上添加车辆改为发送到后台（SprinBoot），实际中域名必须是https的域名，端口号必须是80
              wx.request({
                url: "http://localhost:8080/bike/add",
                data:{
                  // longitude: tlog,
                  // latitude:tlat,
                  location: [tlog, tlat],
                  status:0,
                  bikeNo:100010

                },
                method:'POST',
                success:function(res){
                  //查找单车，然后将单车显示在页面上
                  findBikes(tlog, tlat,that);
                }
              })
            }
          })
         
          break;
        } 
      }
    },
  

  //移动后地图视野变化触发的事件
  regionchange:function(e) {
    var that = this
  // 获取移动后的位置信息，包含滑动开始和滑动结束两个位置信息
  var etype = e.type;

  // 只捕捉移动后的点的信息
    if (etype=='end') 
    
     this.mapCtx.getCenterLocation({
        success: function (res) {
          var log=res.longitude;
          var lat=res.latitude;
          findBikes(log, lat, that);
        }
      }) 
  }
  ,
    
    //生命周期函数，监听页面初次渲染完成后,创建上下文

   onReady:function(){
     //创建map上下文
     this.mapCtx=wx.createMapContext('myMap')//传递map对象的id

   }

})

/**
 * 自定义方法要定义在最外面
 * 
 * 此方法用于：根据经纬度以及当前页面，向服务器发送请求，服务器的该请求里响应了一个方法，这个方法根据这个参数去数据库查找所有
 * 的单车的信息，并以List<Bike>的形式返回给回调函数的参数
 * 
 * after the request successful
 * 将该集合中的每一辆单车的经纬度以及id等信息遍历并设置到当前页面的markers中，从而在地图上显示出来
 * @param longitude
 * @param latitude
 * @param that
 * 
 */
//
function findBikes(longitude,latitude,that){
  wx.request({
    url:"http://localhost:8080/bike/findNear",
    method:"GET",
    data:{
      longitude: longitude,
      latitude: latitude
    },
    success:function(res){
      // console.log(res)     
//请求成功后，服务器返回的是一个根据经纬度查找出的所有单车的List集合，所以结果集res里有很多的bike对象，遍历这些对象，取出每个bike的经纬度，图片位置以及宽高等，并赋值给bikes。
      var bikes = res.data.map((geoResult)=>{
        return {
          longitude: geoResult.content.location[0],
          latitude: geoResult.content.location[1],
          iconPath: "/images/bike.png",
          width: 30,
          height: 35,
          id: geoResult.content.id
        }
      })

      //将bike的数组set到当前页面中的markers中
      that.setData({
        markers:bikes
      })
      
    }
  })
}
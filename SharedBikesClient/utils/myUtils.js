
//要查找的value(如：status)等信息
function get(key){
  //先从内存中取value
  var value = wx.getStorageSync(key)
  //如果value不存在，则从页面中取
  if ((!value)) {
    //根据用户的状态跳转到对应的页面
    value = getApp().globalData[key];
  }
  return value;
}

//意味着将这个get方法导出去了，在相应的页面的js文件中导入这个js文件即可，如：var myUtils=require("../../utils/myUtils.js")
module.exports={
  get 
}
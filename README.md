# FocusSurfaceView

## 简介
修改开源库：[FocusSurfaceView](https://github.com/dongjunkun/DropDownMenu)，在此表示感谢，喜欢的可以自己参考原库进行改写。

## 增加的功能
* 绘制边框外的文字，可以设置文字内容，颜色，大小
* 参考demo的方向设置，可以使预览图和最后得到的bitmap视角相同，解决某些手机上图片旋转的问题

## 使用方法

Step 1. Add the JitPack repository to your build file

Add it in your root build.gradle at the end of repositories:
```
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
  Step 2. Add the dependency
```
  dependencies {
	        compile 'com.github.wzgl5533:FocusSurfaceView:1.0.1'
	}
```
   
 ## 效果图
![wzgl_demo](https://github.com/wzgl5533/FocusSurfaceView/blob/master/screenshots/wzgl_demo.jpg)


## 附言

Demo仅供参考，感谢原库作者，如果有什么需求也可以自己改写，有问题能帮助的，一定尽力帮助，如有瑕疵请谅解，欢迎指正。
本库源于自己项目的需求。

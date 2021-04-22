[TOC]

# 概述

该目录主要为工具类，是不会与用户发生直接交互的类

## AudioPlayer.java

完成音频的播放，用于在“随心舞蹈”部分播放用户选择的伴奏

## Constant.java

记录了APP需要用到的常量

## DeviceInfo.java

完成部分设备信息的获取，现在仅需要设备的像素宽高，以及基于此的dp值px转换

## GenerateJson.java

帮助生成前后端交互的数据结构，以给定的参数生成JSON

## GestureListener.java

监测用户的操作，辅助监听

## GlobalVariable.java

程序运行时的全局变量，采用单例模式

## Img.java

处理图片相关，包括保存Bitmap，压缩Bitmap，为指定的ImageView展示不同形式的图片

## Json2x.java

生成http_get请求需要的参数字符串

## LandLayoutVideo.java

视频播放器

## MD5encrypt.java

对给定字符串进行MD5加密

## MsgProgress.java

对后端返回的信息进行预处理，当发现错误进行Log

## QYFile.java

文件类，将文件上传等相关逻辑进行封装，方便交互。

## QYrequest.java

使用okhttp的网络交互类，方便进行不同的请求的。

## QYUser.java

封装了部分用户操作

## SampleVideo.java

播放器

## SHA256.java

对文件进行SHA256哈希

## ShowProgressDialog.java

操作进度Dialog

## SwitchVideoModel.java

视频播放器

## TimeTool.java

时间工具，对标准日期进行提取不同信息。可以限制请求的发送频率

## Uri2RealPath.java

将安卓文件管理器返回的Uri转换成url

## VideoClip.java

视频裁剪


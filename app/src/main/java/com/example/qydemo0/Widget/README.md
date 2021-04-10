[TOC]

# 概述

自定义组件，实现特定功能的View

## Dashboard.java

自定义组件，在MainActivity中动态加入，显示用户的大部分信息包括（昵称，个性签名，关注，粉丝，以及各种最近信息）会导向 关注粉丝activity,  用户动态作品activity， 以及渲染队列activity，同时用户信息设置activity也在该组件指向。

## Home.java

自定义组件，展示当前热点信息，同时指向核心功能，舞蹈学习，自由舞蹈，并在下面陈列了相关作品的推荐

## LittleLearnItem.java

自定义组件，是LearnItem中的子项，负责展示更为细致的record

## LittleUserItem.java

是关注粉丝中的子项，展示简介的用户信息

## LittleWorkItem.java

显示作品的简洁信息，用以上传动态时的自己稿件展示

## Post.java

在MainActivity中加入的另一重要组件，完成浏览动态的功能，并以“推荐”,“关注”两类区分动态，同时提供了上传动态的入口

## PostItem.java

单个动态，展示单个动态的所以信息，并可以根据情况初始化不同的点击初始化函数

## QYDialog.java

一定程度上自定义的dialog，用于在不同页面显示不同的dialog

## QYNavigation.java

自定义的导航栏，在主页导向不同的自定义组件

## QYScrollView.java

自定义的滚动组件，支持监听下滑，滑动至底部，滑动至顶部

## RenderItem.java

whc

## RenderQueueItem.java

渲染队列中的项，可以显示进度信息

## SmartItem.java

学习队列的项，可以展示学习的进度，得分以金银铜三种奖牌展示

## SwitchVideoTypeDialog.java

whc

## WorkItem.java

在大部分地方展示的work都会用此组件初始化展示
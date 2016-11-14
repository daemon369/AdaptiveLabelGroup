# AdaptiveLabelGroup

自适应标签容器，支持标签自动换行，支持设置标签水平间距、垂直间距

----

# 效果
<img src="demo.png" width = "540" height = "960" alt="Demo效果" align=center />

----

# 使用方法

## 1. 项目中引入依赖：

项目根目录下build.gradle中加入：
```
allprojects {
    repositories {
        ...
        maven { url "https://jitpack.io" }
    }
}
```
添加依赖：

```
dependencies {
    compile 'com.github.daemon369:AdaptiveLabelGroup:v0.0.4'
}
```
## 2. 布局文件中引用：

可以在布局中直接引用：
```
<FrameLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:padding="10dp">

    <me.daemon.AdaptiveLabelGroup
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:background="#4bd144"
        android:padding="5dp"
        tools:horizontal_divider_size="5dp"
        tools:vertical_divider_size="5dp">

        <TextView
            android:layout_width="50dp"
            android:layout_height="30dp"
            android:background="@drawable/bg_label"
            android:text="testtestjustt...." />

        <Button
            android:layout_width="match_parent"
            android:layout_height="50dp"
            android:background="@drawable/bg_label"
            android:text="测试测试测试" />

        <ImageView
            android:layout_width="40dp"
            android:layout_height="40dp"
            android:background="@drawable/bg_label"
            android:src="@mipmap/ic_launcher"
            android:visibility="gone" />

        <TextView
            android:layout_width="90dp"
            android:layout_height="wrap_content"
            android:background="@drawable/bg_label"
            android:text="falefffffffeaf" />

        <ImageView
            android:layout_width="40dp"
            android:layout_height="40dp"
            android:background="@drawable/bg_label"
            android:src="@mipmap/ic_launcher"
            android:visibility="gone" />

        <TextView
            android:layout_width="wrap_content"
            android:layout_height="70dp"
            android:background="@drawable/bg_label"
            android:text="............................................" />

    </me.daemon.AdaptiveLabelGroup>
</FrameLayout>
```

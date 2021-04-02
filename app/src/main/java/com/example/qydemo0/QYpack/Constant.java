package com.example.qydemo0.QYpack;

import android.content.Context;

import com.example.qydemo0.R;

public class Constant {

    public static final Constant mInstance = new Constant();

    public String username = "username", email = "email", phone = "phone", password = "password";

    public String login_url = "https://api.yhf2000.cn/api/qingying/v1/user/account/login/",
        register_url = "https://api.yhf2000.cn/api/qingying/v1/user/account/register/",
        verify_url = "https://api.yhf2000.cn/api/qingying/v1/user/verify/",
        userInfo_url = "https://api.yhf2000.cn/api/qingying/v1/user/info/",
        file_upload_verify_url = "https://api.yhf2000.cn/api/qingying/v1/file/upload/verify/",
        file_upload_normal_url = "https://api.yhf2000.cn/api/qingying/v1/file/upload/normal/",
        user_fans = "https://api.yhf2000.cn/api/qingying/v1/user/follow/",
        work = "https://api.yhf2000.cn/api/qingying/v1/work/",
        getClas_url = "https://api.yhf2000.cn/api/qingying/v1/systematics/classification/",
        getRecommendation_url_user = "https://api.yhf2000.cn/api/qingying/v1/recommendation/user/",
        modifyUserInfo_url = "https://api.yhf2000.cn/api/qingying/v1/user/info/",
        search_url = "https://api.yhf2000.cn/api/qingying/v1/search/work/";

    public int default_login_way = R.id.fragment_username_login,
        default_register_way = R.id.fragment_username_register,
        default_register2_way = R.id.fragment_username_register2,
        HTTP_OK = 200,
        pre_items = 10,
        MAX_FILE_SIZE = 1024 * 1024 * 50; // 50MB

    public String database = "QYdata";


    private Constant(){

    }

}

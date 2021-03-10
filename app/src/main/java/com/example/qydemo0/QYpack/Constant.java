package com.example.qydemo0.QYpack;

import com.example.qydemo0.R;

public class Constant {

    public String username = "username", email = "email", phone = "phone", password = "password";

    public String login_url = "https://api.yhf2000.cn/api/qingying/v1/user/account/login/",
        register_url = "https://api.yhf2000.cn/api/qingying/v1/user/account/register/";

    public int default_login_way = R.id.fragment_username_login,
        default_register_way = R.id.fragment_username_register,
        default_register2_way = R.id.fragment_username_register2,
        HTTP_OK = 200;

    public String database = "QYdata";

}

<template>
  <div>
    <el-container>


      <!-- 主要部分 -->
      <el-container class="main">
        <el-main>
          <el-header>
            <el-col class="main_menu">
            <el-dropdown trigger="click" @command="handleCommand">
              <span class="el-dropdown-link">
                菜单<i class="el-icon-arrow-down el-icon--right"></i>
              </span>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item command="home">
                  首页
                </el-dropdown-item>
                <el-dropdown-item command="userinfo">
                  {{ $t("common.account") }}
                </el-dropdown-item>
                <el-dropdown-item command="showgoods">
                  {{ $t("common.allgoods") }}
                </el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </el-col>
          </el-header>
          <!-- 根据路由地址显示不同页面 -->
          <router-view @loginsuccess="refreshuserinfo"></router-view>
        </el-main>
      </el-container>


    </el-container>

    <!-- 设置开屏动画，runtime是播放多少毫秒 -->
    <zhezhao v-if="zzshow" :runtime="500"></zhezhao>

  </div>
</template>

<script>
import "animate.css";
import mousesee from "@/components/common/mousesee";
import mouserun from "@/components/common/mouserun";
import zhezhao from "@/components/common/zhezhao";
import AsideBody from "@/views/Home/Home/ChildCmp/AsideBody";
import { timehello } from "@/common/utils";
export default {
  components: {
    AsideBody,
    mousesee,
    mouserun,
    zhezhao,
  },
  computed: {
    activeStyle() {
      return !this.primaryshow ? { color: "#fff" } : {};
    },
  },
  data() {
    return {
      titles: [],
      titlesrouter: [],
      primaryshow: true,
      hourtime: 99999,
      username: "",
      zzshow: false,
    };
  },
  created() {
    if (!window.sessionStorage.getItem("firstrouter")) {
      this.zzshow = true;
    }
    if (!!window.sessionStorage.getItem("language")) {
      this.$i18n.locale = window.sessionStorage.getItem("language");
    }
    //  拿到系统时间
    let myDate = new Date();
    this.hourtime = myDate.getHours();
    //网页刷新可以从session中获取到username
    if (!!window.sessionStorage.getItem("username")) {
      this.username = window.sessionStorage.getItem("username");
    }
    //创建或者拿到二级导航缓存（二级导航内需要用到的数据，需要在此之前被赋值，否则会出现bug）
    if (window.sessionStorage.getItem("firstrouter") == null) {
      //页面是初次创建
      // console.log("网页创造")
      this.firstrouter("home");
    } else {
      //页面刷新，拿出二级导航的路径
      // console.log("网页刷新")
      this.firstrouter(window.sessionStorage.getItem("firstrouter"));
    }
  },
  methods: {
    refreshuserinfo() {
      //拿到用户名，刷新二级导航
      this.username = window.sessionStorage.getItem("username");
      // this.$router.go(0);
      this.firstrouter("userinfo");
    },
    firstrouter(firstrouter) {
      let flag = window.sessionStorage.getItem("flag");

      if (this.$i18n.locale == "zh") {
        switch (firstrouter) {
          case "home":
            if (flag != "normaluser") {
              //用户登录判断     !this.username
              this.titles = ["心情惬意，闲逛一下   🌟 "];
            } else {
              if (this.hourtime >= 5 && this.hourtime < 9)
                this.titles = [`早晨好，${this.username}  ☕`];
              if (this.hourtime >= 9 && this.hourtime < 12)
                this.titles = [`上午好，${this.username}  ☕`];
              if (this.hourtime >= 12 && this.hourtime < 18)
                this.titles = [`下午好，${this.username}  ☕`];
              if (this.hourtime >= 18 && this.hourtime < 20)
                this.titles = [`傍晚好，${this.username}  ☕`];
              if (this.hourtime >= 20 && this.hourtime < 23)
                this.titles = [`晚间好，${this.username}  ☕`];
              if (this.hourtime >= 23 || this.hourtime < 5)
                this.titles = [`夜已深啦，尽早休息     ❤️️`];
            }
            this.$router.push("/home");
            break;
          case "userinfo":
            if (flag != "normaluser") {
              this.titles = ["登录您的帐户 🌟", "登录", "关于俱乐部"];
              this.titlesrouter = ["null", "login", "deposit"];
              this.$router.push("/login");
            } else {
              let time = timehello();
              if (time >= 23 || time < 5) {
                this.titles = [
                  "夜已深啦，尽早休息     ❤️",
                  " 账户一览",
                  "账户设置",
                ];
              } else {
                this.titles = [
                  time,
                  this.username + " 账户一览",
                  "账户设置",
                  "加入俱乐部",
                ];
              }
              this.titlesrouter = [
                "null",
                "userinfo",
                "updateuserinfo",
                "deposit",
              ];
              this.$router.push("/userinfo");
            }
            break;
          case "showgoods":
            this.titles = ["所有商品", "商品", "笔", "墨", "纸", "砚"];
            this.titlesrouter = [
              "null",
              "all",
              "笔",
              "墨",
              "纸",
              "砚",
            ];
            this.$router.push("/all"); //默认跳转all goods
            break;
        }
      } else {
        switch (firstrouter) {
          case "home":
            if (flag != "normaluser") {
              //用户登录判断     !this.username
              this.titles = [" It’s a great day  🌟 "];
            } else {
              if (this.hourtime >= 5 && this.hourtime < 9)
                this.titles = [`Good morning，${this.username}  ☕`];
              if (this.hourtime >= 9 && this.hourtime < 12)
                this.titles = [`Good forenoon${this.username}  ☕`];
              if (this.hourtime >= 12 && this.hourtime < 18)
                this.titles = [`Good afternoon${this.username}  ☕`];
              if (this.hourtime >= 18 && this.hourtime < 20)
                this.titles = [`Good evening${this.username}  ☕`];
              if (this.hourtime >= 20 && this.hourtime < 23)
                this.titles = [`Good night${this.username}  ☕`];
              if (this.hourtime >= 23 || this.hourtime < 5)
                this.titles = [`Good night     ❤️️`];
            }
            this.$router.push("/home");
            break;
          case "userinfo":
            if (flag != "normaluser") {
              this.titles = ["login account 🌟", "login in", "club"];
              this.titlesrouter = ["null", "login", "deposit"];
              this.$router.push("/login");
            } else {
              let time = timehello();
              if (time >= 23 || time < 5) {
                this.titles = ["Good night     ❤️️", "account", "set account"];
              } else {
                this.titles = [
                  time,
                  this.username + "’account",
                  "set account",
                  "join club",
                ];
              }
              this.titlesrouter = [
                "null",
                "userinfo",
                "updateuserinfo",
                "deposit",
              ];
              this.$router.push("/userinfo");
            }
            break;
          case "showgoods":
            this.titles = [
              "All Goods",
              "all",
              "笔",
              "墨",
              "纸",
              "砚",
            ];
            this.titlesrouter = [
              "null",
              "all",
              "笔",
              "墨",
              "纸",
              "砚",
            ];
            this.$router.push("/all"); //默认跳转all goods
            break;
        }
      }

      //把二级导航放到session
      window.sessionStorage.setItem("firstrouter", firstrouter);
    },
    changeprimaryshow() {
      this.primaryshow = !this.primaryshow;
      //  二级菜单页面切换
    },
    newwindow(path) {
      let routeUrl = this.$router.resolve(path);
      window.open(routeUrl.href, "_blank");
    },
    // 国际化语言切换
    languagechange(value) {
      // this.$i18n.locale = value; // 该页面可以响应式的数据会产生突变,可以在create阶段进行改变
      window.sessionStorage.setItem("language", value);
      this.$router.go(0);
    },
    handleCommand(command) {  //获取菜单被点击时的事件
      switch (command) {
        case "/shopdetail":
          console.log(command);
          this.newwindow(command);
          break;
        default:
          console.log(command);
          this.firstrouter(command);
          break;
      }
    }
  },
};
</script>

<style lang="less" scoped>
.bossaside {
  height: 100vh;
  width: 28vw !important;
  position: fixed;
  top: 0;
  left: 0;
}

////元素fixed脱离文档流，用来占位元素
.bossasidefeakdiv {
  height: 100vh;
  width: 28vw !important;
}

//侧边栏头部
.asidehead {
  display: flex;
  height: 18vh !important;
  padding: 2vh 1vw;

  //一级目录
  .primary {
    height: 100%; //高度填充
    flex: 6 1 auto;
    display: flex;
    align-items: center;

    // padding-right: .3vw ;
    //background-color: #ff8198;
    ul {
      display: flex;
      width: 100%;
      //子元素间隔开，或者使用margin
      justify-content: space-between;
      list-style: none;

      li {
        flex: 1;

        a {
          font-size: 1.3vw;
          font-weight: 700;
          white-space: nowrap; //字体超出不换行
        }
      }
    }
  }

  //图片
  a {
    height: 100%; //高度填充
    width: 3vw;
    flex-grow: 1;
    position: relative;
    display: flex;
    align-items: center;
  }

  .logo img {
    width: 80%;
    padding-bottom: 1.3vw;
  }

  .tirgger img {
    width: 50%;
    //图片贴右
    position: absolute;
    right: 0;
  }

  .close img {
    width: 30%;
    //图片贴右
    position: absolute;
    right: 0;
  }
}

//侧边栏内容区
.aside_body {
  height: 72vh;
  width: 100%;
  display: flex;
  justify-content: center; //水平居中
  align-items: center;
  //flex-direction: row;    设置主轴
  //align-item:center;    这个是子元素的属性少一个s
}

//侧边栏垫高
.aside_foot {
  height: 10vh;
}

.primaryshow {
  transition: opacity 0.5s;
}

.primaryhidden {
  transition-duration: 0.5s;
  opacity: 0;
}

.el-main {
  background-color: rgba(241, 246, 209, 0.5);
  color: #333;
  padding: 0px;
}

//改部分子组件的样式
.m1 {
  /deep/ .ms {
    background-color: red;
    position: fixed;
    bottom: 5vh;
    right: 5vw;
  }
}

.m2 {
  /deep/ .ms {
    background-color: green;
    position: fixed;
    bottom: 5vh;
    left: 33vw;
  }
}

.main_menu {
  text-align: right;
  padding: 10px;
}

.el-dropdown-link {
  font-size: 20px;
  cursor: pointer;
  color: #409EFF;
}

.el-icon-arrow-down {
  font-size: 12px;
}

.demonstration {
  display: block;
  color: #8492a6;
  font-size: 14px;
  margin-bottom: 20px;
}

el-dropdown-item p {
  width: 100%;
}
</style>
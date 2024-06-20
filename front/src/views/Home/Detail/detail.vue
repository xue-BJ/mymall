<template>
  <div>
    <el-container style="background-color: rgba(241, 246, 209, 0.5)">
      <el-header>
        <el-page-header
          @back="goBack"
          :content="getpageheadercontent"
          :title="getpageheadertitle"
        ></el-page-header>
      </el-header>
      <el-main>
        <!--       轮播图片-->
        <el-carousel
          indicator-position="outside"
          :interval="5000"
          arrow="hover"
          height="260px"
        >
          <el-carousel-item
            v-for="(item, index) in goods.imglist"
            :key="index"
            class="detaillunbo"
          >
            <div class="image">
              <img v-lazy="item.goodsimg" />
            </div>
          </el-carousel-item>
        </el-carousel>
        <!--        商品介绍-->
        <div class="content">
          <div class="title">
            <p>{{ goods.goodstitle }}</p>
          </div>
          <div class="price">
            <span>{{ $t("common.sellcount") }}:{{ goods.sellcount }}</span>
            <p style="color: rgba(250, 71, 71, 0.8); font-size: 2em">
              {{ $t("common.nowprice") }}: ￥{{ goods.nowprice }}
            </p>
          </div>
          <div class="buy">
            <button class="buybtn" @click="buy">
              {{ $t("common.buy") }}
            </button>
            <el-drawer
              title="您的订单下单成功~请查收~"
              :visible.sync="drawer"
              size="50%"
            >
              <div>
              </div>
            </el-drawer>
          </div>
          <div class="describe">
            <p>
              {{ goods.goodsdescribe }}
            </p>
          </div>
          <div>
            <p>商品细节大图</p>
            <el-image
              v-for="item in goods.imglist"
              :key="item"
              :src="item.goodsimg"
              lazy
            ></el-image>
          </div>
        </div>
      </el-main>
    </el-container>
  </div>
</template>

<script>
/*引入修改的el样式*/
import "assets/css/user_detaillELUI.css";
import sizetable from "views/Home/Detail/childcmp/sizetable";
import { secondbuy } from "@/network/goods";
export default {
  name: "detail",
  components: {
    sizetable,
  },
  data() {
    return {
      id: "",
      goods: {},
      choosesize: "",
      drawer: false,
      innerDrawer: false,
      animate: false,
      items: [
        //消息列表对应的数组
        { name: "🐴云云" },
        { name: "云🐴云" },
        { name: "云云🐴" },
        { name: "军🐴雷" },
        { name: "🐴军雷" },
        { name: "刘🐴东" },
        { name: "刘强🐴" },
        { name: "强🐴东" },
        { name: "🐴yueyue" },
        { name: "❤️🐴❤️" },
        { name: "❤️❤️🐴" },
        { name: "❤️🐴❤️" },
      ],
    };
  },
  computed: {
    getpageheadercontent() {
      if (this.$i18n.locale == "en") {
        return "Goods Detail";
      } else {
        return "商品详情";
      }
    },
    getpageheadertitle() {
      if (this.$i18n.locale == "en") {
        return "Go Back";
      } else {
        return "返回";
      }
    },
  },
  created() {
    this.id = this.$route.params.id;
    this.goods = this.$store.getters.getgoodsbyid(this.id);
  },
  mounted() {
    this.timer1 = setInterval(this.scroll, 1000);
  },
  methods: {
    scroll() {
      let con1 = this.$refs.con1;
      con1.style.marginTop = "-30px";
      this.animate = !this.animate;


      var that = this; // 在异步函数中会出现this的偏移问题，此处一定要先保存好this的指向
      setTimeout(function () {
        that.items.push(that.items[0]);
        that.items.shift();
        con1.style.marginTop = "0px";
        that.animate = !that.animate; // 这个地方如果不把animate 取反会出现消息回滚的现象，此时把ul 元素的过渡属性取消掉就可以完美实现无缝滚动的效果了
      }, 500);
    },
    mEnter() {
      clearInterval(this.timer1);
    },
    mLeave() {
      this.timer1 = setInterval(this.scroll, 1000);
    },
    handleClose(done) {
      this.$confirm("小站作者感激涕零的给你点了个大大的赞~thanks❤️")
        .then((_) => {
          this.$confirm("感谢again~");
          done();
        })
        .catch((_) => {});
    },
    goBack() {
      this.$router.go(-1);
    },
    sizeemit(size) {
      if (size == 9999999) {
        this.choosesize = "";
      } else {
        this.choosesize = size;
      }
    },
    buy() {
      //  用户下单
      if (!this.$store.state.user.id) {
        this.$confirm("您没有登录哦，请先登录，再选购呦");
        this.$router.push("/home");
        // this.$router.push("/home/login")     无法跳转无关子路由
      } else {
        if (true) {
          // 默认付款成功  发送请求实现用户的订单添加到对应的用户订单表中
          // console.log(this.$store.state.user.id+'===='+this.id+"======"+s)
          secondbuy(this.$store.state.user.id, this.id, "s").then(
            (res) => {
              if (res.data == "success") {
                this.$store.state.havebug = true; //购买标记
                this.drawer = true;
              }
            }
          );
        } else {
          this.$message.warning("~未选择商品具体参数信息哦~");
        }
      }
    },
    newwindow(path) {
      window.open(path, "_blank");
    },
  },
};
</script>

<style lang="less" scoped>
#box {
  width: 100%;
  height: 200px;
  line-height: 30px;
  overflow: hidden;
  padding-left: 30px;
  // border: 1px solid black;
  transition: all 0.5s;
  margin: 50px 0;
}
.anim {
  transition: all 0.5s;
}
#con1 li {
  list-style: none;
  line-height: 30px;
  height: 30px;
}
/deep/ .el-drawer {
  width: 30% !important;
  background-color: rgba(241, 246, 209, 1);
}
.el-header {
  height: 10vh !important;
  .el-page-header {
    margin: 15px;
    //font-family: monospace;
  }
}
.el-main {
  height: 90vh !important;
  padding: 20px 60px;
  display: flex;
  .el-carousel {
    width: 40vw;
  }

  .content {
    width: 100%;
    margin-left: 40px;
    padding: 20px;
    font-size: 1.7rem;
    color: rgba(0, 0, 0, 0.7);
    text-align: left;
    //font-family: monospace;
    .title {
      font-size: 1.9rem;
      font-weight: 1000;
      text-align: center;
      margin-bottom: 1.5rem;
    }
    p {
      white-space: normal;
      margin-bottom: 1.2rem;
    }
    .describe {
      padding: 20px;
      font-size: 2rem;
      text-align: left;
      letter-spacing: 0.1rem;
      font-family: monospace;
    }
    span {
      float: right;
    }
    .buy {
      width: 100%;
      display: flex;
      align-items: center;
      justify-content: center;
      .buybtn {
        width: 200px;
        height: 70px;
        border-radius: 50px;
        border: 2px solid rgba(12, 12, 12, 0.1);
        transition: 0.05s;
        background-color: rgba(255, 253, 239, 0);
        font-size: 2rem;
        font-weight: 400;
      }
      .buybtn:active {
        color: rgba(255, 129, 152, 0.5);
        box-shadow: 7px 0px 2px rgba(12, 12, 12, 0.1);
      }
    }
  }
}
//element内部的样式需要在去全局样式中修改，在组件中修改无效
.el-carousel {
  margin-top: 80px;
  .image {
    width: 100%;
    img {
      width: 100%;
    }
  }
}
</style>
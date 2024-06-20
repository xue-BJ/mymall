<template>
  <div>
    <el-container>
      <el-header class="head">

        <div class="a" style="z-index: 9">
          <!--        搜索框-->
          <input type="text" class="b" v-model="searchquery" placeholder="Name OR Describe" />
          <a class="c" @click="search">
            <img src="http://panshi-on.oss-cn-hangzhou.aliyuncs.com/yunxiaoding-mini/other/mall/img/搜索框2.png"
              style="width: 30px" />
          </a>
        </div>
      </el-header>

      <el-main>
        <aside-body :titles="this.titles" :titlesrouter="this.titlesrouter"></aside-body>
        <router-view></router-view>
      </el-main>
    </el-container>
  </div>
</template>

<script>
import mouserun from "@/components/common/mouserun";
import mousesee from "@/components/common/mousesee";
import Card from "@/components/content/Card/Card";
import AsideBody from '../Home/ChildCmp/AsideBody.vue';
export default {
  name: "ShowGoods",
  data() {
    return {
      searchquery: "",
      activeName: 'second',
      titles: [],
      titlesrouter: [],
    };
  },
  components: {
    mouserun,
    mousesee,
    Card,
    AsideBody
  },
  created() {
    //DONE:修改titles和titlesrouter为文房四宝相关数据
    this.titles = [
      "All Goods",
      "全部",
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
  },
  methods: {
    search() {
      this.$router.push({
        path: "/search",
        query: { query: this.searchquery },
      });
    },
    handleClick(tab, event) {
      console.log(tab, event);
    }
  },
};
</script>

<style lang="less" scoped>
.head {
  position: relative;
  //margin-bottom: -30px;
}

.a {
  position: absolute;
  top: 20%;
  left: 5%;
  background-color: #2f3640;
  height: 70px;
  border-radius: 70px;
  padding: 5px;
}

.a:hover .b {
  width: 400px;
  padding: 0 1.5rem;
}

.a:hover .c {
  background-color: wheat;
}

.b {
  border: none;
  background: none;
  /* 取消选中搜索框时的外边框 */
  outline: none;
  width: 0;
  padding: 0;
  transition: 0.4s;
  line-height: 60px;
  font-size: 2rem;
  color: wheat;
}

.c {
  color: #e71a40;
  float: right;
  width: 60px;
  height: 60px;
  border-radius: 60px;
  background-color: #2f3640;
  display: flex;
  justify-content: center;
  align-items: center;
  transition: 0.4s;
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

.el-main {
  //padding: 4px;
}
</style>

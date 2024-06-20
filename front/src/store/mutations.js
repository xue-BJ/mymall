import {
  SET_USER,
  SET_GOODSINFO
} from "./mutation-types";

export default {

  [SET_USER](state, user) {
    state.user = user;
  },
  [SET_GOODSINFO](state, goodsinfo) {
    state.goodslist = goodsinfo;
  }
}

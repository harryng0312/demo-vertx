import { defineStore } from 'pinia'

const useCounterStore = defineStore({
  id: 'counter',
  state: () => ({
    counter: 0
  }),
  getters: {
    doubleCount: (state) => state.counter * 2
  },
  actions: {
    increment() {
      this.counter++
    }
  }
});

const getTokenStore = defineStore({
  id: "token",
  state: () => ({
    token: ""
  }),
  actions: {}
});

export {useCounterStore, getTokenStore};

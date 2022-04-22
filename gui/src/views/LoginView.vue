<script lang="ts">
import {getTokenStore} from "@/stores/counter";

export default {
  data(){
    return {
      username: "",
      password: ""
    };
  },
  methods: {
    login(evt: Event) {
      console.log(`Stack size: ${this.$router.getRoutes().length}`);
      if(this.username !== ""){
        let token = getTokenStore().token;
        getTokenStore().token = this.username;
        this.$router.push("/");
      }
    },
  },
}
</script>

<template>
  <div class="center">
    <form id="authForm" method="post" action="login" onsubmit="return false;">
      <table style="border: 0;">
        <thead>
        <tr>
          <td v-text="$t(`login.title`)"></td>
        </tr>
        </thead>
        <tbody>
        <tr>
          <td v-text="$t('login.username')"></td>
        </tr>
        <tr>
          <td><input id="txtUsername" name="txtUsername" v-model="username"
                     :placeholder="$t('login.username')" autocomplete="off"/>
          </td>
        </tr>
        <tr>
          <td v-text="$t('login.password')"></td>
        </tr>
        <tr>
          <td><input type="password" id="txtPassword" name="txtPassword" v-model="password"
                     :placeholder="$t('login.password')" autocomplete="off"/></td>
        </tr>
        <tr>
          <td>&nbsp;</td>
        </tr>
        </tbody>
        <tfoot>
        <tr>
          <td>
            <input type="submit" id="submit" :value="$t('login.submit')" @click="login"/>
            <input id="tokenId" name="tokenId" type="hidden"/>
          </td>
        </tr>
        </tfoot>
      </table>
    </form>
  </div>
</template>

<style>
@media (min-width: 1024px) {
  .about {
    min-height: 100vh;
    display: flex;
    align-items: center;
  }
}
</style>

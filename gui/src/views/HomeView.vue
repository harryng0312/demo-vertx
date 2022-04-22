<script setup lang="ts">
import HomeLayout from "@/layouts/HomeLayout.vue";
import TheWelcome from '@/components/TheWelcome.vue'
</script>

<template>
  <home-layout>
    <template #body>
      <the-welcome/>
    </template>
  </home-layout>
</template>

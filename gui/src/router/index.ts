import { createRouter, createWebHistory } from 'vue-router';
import {isAuthenticated} from "@/ts/Authentication";
import HomeView from '@/views/HomeView.vue';
import LoginView from '@/views/LoginView.vue';

const router = createRouter({
  // history: createWebHistory(import.meta.env.BASE_URL),
  history: createWebHistory("/static"),
  routes: [
    {
      path: "/login",
      name:"login",
      component: LoginView
    },{
      path: '/',
      name: 'home',
      component: HomeView,
    },{
      path: '/about',
      name: 'about',
      // route level code-splitting
      // this generates a separate chunk (About.[hash].js) for this route
      // which is lazy-loaded when the route is visited.
      component: () => import('../views/AboutView.vue')
    },{
      path: '/:pathMatch(.*)*',
      name: "notFound",
      component: () => import("../views/NotFound.vue")
    }
  ]
});
router.beforeEach((to, from, next)=>{
  if(to.name !== "login" && to.name !== "notFound" && !isAuthenticated(to, from)){
    next({name:"login"});
  }else{
    next();
  }
});

export default router

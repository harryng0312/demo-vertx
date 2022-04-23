import {getTokenStore} from "@/stores/counter";

function isAuthenticated(to, from) {
    let token = getTokenStore().token;
    console.log(`from:[${from.path}] to:[${to.path}]`);
    return token !== "";
}

export { isAuthenticated };
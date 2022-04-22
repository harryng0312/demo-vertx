import { createI18n } from "vue-i18n";
import enMessage from "./enMessage.json";
import viMessage from "./viMessage.json";

const messages = {
    "en": enMessage,
    "vi": viMessage
};

const i18n = createI18n({
    locale: 'en',
    messages: messages
});

export default i18n;
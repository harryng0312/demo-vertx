package org.harryng.demo.vertx.config;

import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.messageresolver.AbstractMessageResolver;
import org.thymeleaf.util.Validate;

import java.util.*;

public class TemplateMessageResolver extends AbstractMessageResolver {
    private final static Logger logger = LoggerFactory.getLogger(TemplateMessageResolver.class);
    private final Properties defaultMessages = new Properties();
    private final Map<Locale, Properties> localizedMessages = new LinkedHashMap<>();

    public TemplateMessageResolver(String basename, Locale... locales) {
        super();
        readMessageBundles(basename, locales);
    }

    private void readMessageBundles(String basename, Locale[] locales) {
        var defaultRs = PropertyResourceBundle.getBundle(basename);
        var iteDefaultRs = defaultRs.getKeys();
        while (iteDefaultRs.hasMoreElements()) {
            var key = iteDefaultRs.nextElement();
            defaultMessages.put(key, defaultRs.getString(key));
        }
        if (locales != null && locales.length > 0) {
            for (Locale locale : locales) {
                var rs = PropertyResourceBundle.getBundle(basename, locale);
                var props = new Properties();
                var iteRs = rs.getKeys();
                while (iteRs.hasMoreElements()) {
                    var key = iteRs.nextElement();
                    props.put(key, rs.getString(key));
                }
                localizedMessages.put(locale, props);
            }
        }
    }

    @Override
    public String resolveMessage(ITemplateContext context, Class<?> origin, String key, Object[] messageParameters) {
        Validate.notNull(context, "Context cannot be null");
        Validate.notNull(context.getLocale(), "Locale in context cannot be null");
        Validate.notNull(key, "Message key cannot be null");
        Properties messages = localizedMessages.getOrDefault(context.getLocale(), defaultMessages);
        if (messages != null && messages.get(key) != null)
            return messages.getProperty(key);
        return null;
    }

    @Override
    public String createAbsentMessageRepresentation(ITemplateContext context, Class<?> origin, String key, Object[] messageParameters) {
        Validate.notNull(key, "Message key cannot be null");
        if (context.getLocale() != null) {
            return "??" + key + "_" + context.getLocale().toString() + "??";
        }
        return "??" + key + "_" + "??";
    }
}

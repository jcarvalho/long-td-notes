package pt.ist.longtx.api;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ist.fenixframework.longtx.LongTransaction;
import pt.ist.fenixframework.longtx.TransactionalContext;

@WebFilter("/api/*")
public class LongTxFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(LongTxFilter.class);

    public static final String CONTEXT_KEY = "__LONG_TX_CONTEXT__";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
        HttpSession session = ((HttpServletRequest) request).getSession(false);
        if (session != null) {
            TransactionalContext ctx = (TransactionalContext) session.getAttribute(CONTEXT_KEY);
            if (ctx != null) {
                try {
                    LongTransaction.setContextForThread(ctx);
                    logger.debug("Setting context {} for thread", ctx);
                    chain.doFilter(request, response);
                    return;
                } finally {
                    LongTransaction.removeContextFromThread();
                }
            }
        }
        logger.debug("No TransactionalContext found, moving on...");
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }

}

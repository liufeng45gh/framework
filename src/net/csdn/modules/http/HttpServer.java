package net.csdn.modules.http;

import com.google.inject.Inject;
import com.xn.pento.common.Result;
import com.xn.pento.util.Log;
import net.csdn.ServiceFramework;
import net.csdn.common.exception.*;
import net.csdn.common.logging.CSLogger;
import net.csdn.common.logging.Loggers;
import net.csdn.common.settings.Settings;
import net.csdn.jpa.JPA;
import net.csdn.modules.http.support.HttpStatus;
import net.sf.json.*;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlets.MultiPartFilter;
import org.eclipse.jetty.webapp.WebAppContext;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;


/**
 * BlogInfo: william
 * Date: 11-9-2
 * Time: 下午1:29
 */
public class HttpServer {
    private Server server;
    private CSLogger logger = Loggers.getLogger(getClass());

    private RestController restController;
    private boolean disableMysql = false;
    private Settings settings;

    @Inject
    public HttpServer(Settings settings, RestController restController) {
        this.settings = settings;
        this.restController = restController;
        disableMysql = settings.getAsBoolean(ServiceFramework.mode + ".datasources.mysql.disable", false);
        server = new Server();
        SelectChannelConnector connector = new SelectChannelConnector();

        connector.setPort(settings.getAsInt("http.port", 8080));
        server.addConnector(connector);

        WebAppContext servletHandler = new WebAppContext();
        servletHandler.setContextPath("/");
        servletHandler.setWar("src/com/xn/pento/view");
        RootServlet rootServlet = new RootServlet();

        String[] resources = settings.getAsArray("application.servlet");
        for (String resource : resources) {
            servletHandler.addServlet(new ServletHolder(rootServlet), "/" + resource + "/*");
        }

        ResourceHandler staticResourceHandler = new ResourceHandler();
        String filePath = settings.get(ServiceFramework.mode + ".datasources.filestore.path");
        staticResourceHandler.setResourceBase(filePath);
        staticResourceHandler.setDirectoriesListed(false);

        ContextHandler staticContextHandler = new ContextHandler();
        staticContextHandler.setContextPath("/static");
        staticContextHandler.setHandler(staticResourceHandler);

        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[]{
                staticContextHandler,
                servletHandler,
        });

        server.setHandler(handlers);
    }

    class RootServlet extends HttpServlet {

        @Override
        protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            doService(req, resp);
        }

        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            doService(req, resp);
        }

        @Override
        protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            doService(req, resp);
        }

        @Override
        protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            doService(req, resp);
        }

        @Override
        protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            doService(req, resp);
        }

        @Override
        protected void doTrace(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            doService(req, resp);
        }

        @Override
        protected void doHead(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            doService(req, resp);
        }

        private void doService(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
            class DefaultRestResponse implements RestResponse {
                private String template;
                private Map<String, Object> objectMap;
                private String redirectUrl;
                private String content;
                private byte[] contentByte;
                private int status = HttpStatus.HttpStatusOK;
                private String content_type = "application/json; charset=UTF-8";

                public void write(String content) {
                    this.content = content;
                }

                @Override
                public void write(String content, ViewType viewType) {
                    write(HttpStatus.HttpStatusOK, content, viewType);
                }

                public void write(int httpStatus, String content) {
                    this.content = content;
                    this.status = httpStatus;
                }

                @Override
                public void write(int httpStatus, String content, ViewType viewType) {
                    this.status = httpStatus;
                    if (viewType == ViewType.html) {
                        content_type = "text/html; charset=utf-8";
                    } else if (viewType == ViewType.json) {
                        content_type = "application/json; charset=utf-8";
                    }
                    this.content = content;
                }


                public void write(byte[] contentByte) {
                    this.contentByte = contentByte;
                }

                public void render(String template, Map<String, Object> objectMap) {
                    this.template = template;
                    this.objectMap = objectMap;
                    // render jsp template
                    content_type = "text/html; charset=UTF-8";
                }

                public void redirect(String url) {
                    this.redirectUrl = url;
                }

                @Override
                public void cookie(String name, String value) {
                    resp.addCookie(new Cookie(name, value));
                }

                @Override
                public void cookie(Map cookieInfo) {
                    Cookie cookie = new Cookie((String) cookieInfo.get("name"), (String) cookieInfo.get("value"));
                    if (cookieInfo.containsKey("domain")) {
                        cookie.setDomain((String) cookieInfo.get("domain"));
                    }
                    if (cookieInfo.containsKey("max_age")) {
                        cookie.setMaxAge((Integer) cookieInfo.get("max_age"));
                    }
                    if (cookieInfo.containsKey("path")) {
                        cookie.setPath((String) cookieInfo.get("path"));
                    }
                    if (cookieInfo.containsKey("secure")) {
                        cookie.setSecure((Boolean) cookieInfo.get("secure"));
                    }

                    if (cookieInfo.containsKey("version")) {
                        cookie.setVersion((Integer) cookieInfo.get("version"));
                    }
                    resp.addCookie(cookie);
                }

                public String content() {
                    return this.content;
                }

                @Override
                public Object originContent() {
                    return null;
                }

                @Override
                public RestResponse originContent(Object obj) {
                    return null;
                }

                public void send() throws Exception {
                    if (this.redirectUrl != null) {
                        resp.sendRedirect(this.redirectUrl);
                        return;
                    }
                    if (this.template != null) {
                        if (objectMap != null) {
                            for (String key : objectMap.keySet()) {
                                req.setAttribute(key, objectMap.get(key));
                            }
                        }

                        getServletContext().getRequestDispatcher(this.template).forward(req, resp);
                        return;
                    }
                    if (content != null) {
                        output(content);
                        return;
                    }
                    if (contentByte != null) {
                        outputAsByte(contentByte);
                        return;
                    }
                }

                public void error(Exception e) throws IOException {
                    Result result = Result.fail(e.getMessage());

                    if (e instanceof RecordNotFoundException) {
                        status = HttpStatus.HttpStatusNotFound;
                    } else if (e instanceof RecordExistedException || e instanceof ArgumentErrorException || e instanceof JSONException) {
                        status = HttpStatus.HttpStatusBadRequest;
                    } else if (e instanceof ValidateErrorException) {
                        result = Result.result(false, e.getMessage(), ((ValidateErrorException)e).getResults());
                        status = HttpStatus.HttpStatusBadRequest;
                    } else if (e instanceof AuthException) {
                        status = HttpStatus.HttpStatusNotAuthorize;
                    } else {
                        status = HttpStatus.HttpStatusSystemError;
                    }

                    resp.setStatus(status);

                    if (req.getRequestURI().contains(".json")) {
                        output(result.toJson());
                    } else if (this.redirectUrl != null) {
                        // redirect to custom error page
                        resp.sendRedirect(this.redirectUrl);
                    } else {
                        output(e.getMessage());
                    }
                }

                public void output(String msg) throws IOException {
                    resp.setContentType(content_type);
                    resp.setStatus(status);
                    //httpServletResponse.setContentLength(msg.length());
                    PrintWriter printWriter = resp.getWriter();
                    printWriter.write(msg);
                    printWriter.flush();
                    printWriter.close();
                }

                public void outputAsByte(byte[] msg) throws IOException {
                    //httpServletResponse.setContentType("application/json; charset=UTF-8");
                    resp.setStatus(status);
                    ServletOutputStream outputStream = resp.getOutputStream();
                    outputStream.write(msg);
                    outputStream.flush();
                    outputStream.close();
                }

                public void internalDispatchRequest() throws Exception {
                    RestController controller = restController;
                    RestRequest restRequest = new DefaultRestRequest(req);
                    try {
                        long time1 = System.currentTimeMillis();
                        controller.dispatchRequest(restRequest, this);
                        Log.info("execute time for %s :[%d]", req.getRequestURI(), (System.currentTimeMillis() - time1));
                    } catch (Exception e) {
                        ExceptionHandler.renderHandle(e);
                    }
                }
            }

            DefaultRestResponse channel = new DefaultRestResponse();
            try {
                channel.internalDispatchRequest();
                if (!disableMysql) {
                    JPA.getJPAConfig().getJPAContext().closeTx(false);
                }
                channel.send();
            } catch (Exception e) {
                e.printStackTrace();
                //回滚
                if (!disableMysql) {
                    JPA.getJPAConfig().getJPAContext().closeTx(true);
                }
                channel.error(e);
            }

        }
    }

    public void start() {
        try {
            server.start();
            server.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            server.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void join() {
        try {
            server.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}

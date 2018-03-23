/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.onlinedemo;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

public class Main extends HttpServlet
{
    private Handler handler;

    public void init() {
        String realPath = getServletContext().getRealPath("/");

        handler = new Handler(
                new WorkerPoolFactory(
                        new File(realPath + "../tins-requests.txt"),
                        new File(realPath + "../tins-exceptions.txt")
                )
        );
    }

    public void destroy() {
        handler.destroy();
    }

    @SuppressWarnings("checkstyle:throwscount")
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        handler.doPost(request, response);
    }
}

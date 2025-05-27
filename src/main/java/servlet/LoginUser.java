package servlet;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dao.AlumnowebJpaController;
import dto.Alumnoweb;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import util.JwtUtil;

@WebServlet(name = "LoginUser", urlPatterns = {"/login"})
public class LoginUser extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        JsonObject jsonObject = new JsonObject();
        Gson gson = new Gson();

        try ( PrintWriter out = response.getWriter()) {
            BufferedReader reader = request.getReader();
            JsonObject json = gson.fromJson(reader, JsonObject.class);
            String dni = json.get("dni").getAsString();
            String pass = json.get("pass").getAsString();
            AlumnowebJpaController ajc = new AlumnowebJpaController();
            Alumnoweb alumno = ajc.validar(dni, pass);
            if (alumno != null) {
                String token = JwtUtil.generarToken(alumno.getNombEstdWeb());
                jsonObject.addProperty("result", "ok");
                jsonObject.addProperty("token", token);
            } else {
                jsonObject.addProperty("result", "not");
            }
            out.print(gson.toJson(jsonObject));
            out.flush();

        } catch (Exception e) {
            // Enviar respuesta de error en JSON para que JS pueda manejarla
            try ( PrintWriter out = response.getWriter()) {
                jsonObject.addProperty("result", "error");
                jsonObject.addProperty("message", e.getMessage());
                out.print(gson.toJson(jsonObject));
                out.flush();
            }
            e.printStackTrace();
        }
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }
}

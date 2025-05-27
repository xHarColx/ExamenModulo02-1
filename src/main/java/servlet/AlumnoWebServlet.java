/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package servlet;

import com.google.gson.Gson;
import dao.AlumnowebJpaController;
import dto.Alumnoweb;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author harol
 */
@WebServlet(name = "AlumnoWebServlet", urlPatterns = {"/AlumnoWebServlet"})
public class AlumnoWebServlet extends HttpServlet {

    private EntityManagerFactory emf;

    @Override
    public void init() throws ServletException {
        emf = Persistence.createEntityManagerFactory("com.mycompany_Preg01_war_1.0-SNAPSHOTPU");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String token = request.getParameter("token");
        boolean tokenValido = util.JwtUtil.validarToken(token);

        if (!tokenValido) {
            enviarError(response, "Token inválido", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        EntityManager em = emf.createEntityManager();
        try {
            List<Alumnoweb> alumnos = em.createNamedQuery("Alumnoweb.findAll", Alumnoweb.class).getResultList();

            JSONArray jsonArray = new JSONArray();
            AlumnowebJpaController alumnoDAO = new AlumnowebJpaController(emf);

            for (Alumnoweb a : alumnos) {
                JSONObject obj = new JSONObject();
                obj.put("codiEstdWeb", a.getCodiEstdWeb());
                obj.put("ndniEstdWeb", a.getNdniEstdWeb());
                obj.put("appaEstdWeb", a.getAppaEstdWeb());
                obj.put("apmaEstdWeb", a.getApmaEstdWeb());
                obj.put("nombEstdWeb", a.getNombEstdWeb());
                obj.put("fechNaciEstdWeb", a.getFechNaciEstdWeb().toString());

                // Aquí debes implementar tu método calcularEdad(...) si lo deseas
                int edad = alumnoDAO.calcularEdad(a.getFechNaciEstdWeb());
                obj.put("edad", edad);

                obj.put("logiEstd", a.getLogiEstd());
                

                jsonArray.put(obj);
            }

            response.setContentType("application/json;charset=UTF-8");
            try ( PrintWriter out = response.getWriter()) {
                out.print(jsonArray.toString());
            }
        } finally {
            em.close();
        }
    }

    // Métodos auxiliares
    private void enviarRespuesta(HttpServletResponse response, String mensaje) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        try ( PrintWriter out = response.getWriter()) {
            out.print(new JSONObject().put("resultado", mensaje).toString());
        }
    }

    private void enviarError(HttpServletResponse response, String mensaje, int codigo) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(codigo);
        try ( PrintWriter out = response.getWriter()) {
            out.print(new JSONObject().put("error", mensaje).toString());
        }
    }

    @Override
    public void destroy() {
        if (emf != null) {
            emf.close();
        }
    }

}

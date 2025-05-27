package dao;

import dao.exceptions.NonexistentEntityException;
import dto.Alumnoweb;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import util.HashPass;

public class AlumnowebJpaController implements Serializable {

    public AlumnowebJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("com.mycompany_Preg01_war_1.0-SNAPSHOTPU");

    public AlumnowebJpaController() {
    }

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Alumnoweb alumnoweb) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(alumnoweb);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Alumnoweb alumnoweb) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            alumnoweb = em.merge(alumnoweb);
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = alumnoweb.getCodiEstdWeb();
                if (findAlumnoweb(id) == null) {
                    throw new NonexistentEntityException("The alumnoweb with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Alumnoweb alumnoweb;
            try {
                alumnoweb = em.getReference(Alumnoweb.class, id);
                alumnoweb.getCodiEstdWeb();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The alumnoweb with id " + id + " no longer exists.", enfe);
            }
            em.remove(alumnoweb);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Alumnoweb> findAlumnowebEntities() {
        return findAlumnowebEntities(true, -1, -1);
    }

    public List<Alumnoweb> findAlumnowebEntities(int maxResults, int firstResult) {
        return findAlumnowebEntities(false, maxResults, firstResult);
    }

    private List<Alumnoweb> findAlumnowebEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Alumnoweb.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Alumnoweb findAlumnoweb(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Alumnoweb.class, id);
        } finally {
            em.close();
        }
    }

    public int getAlumnowebCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Alumnoweb> rt = cq.from(Alumnoweb.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    public Alumnoweb findClie(String dni) {
        EntityManager em = getEntityManager();
        try {
            Query query = em.createNamedQuery("Alumnoweb.findByNdniEstdWeb");
            query.setParameter("ndniEstdWeb", dni);
            return (Alumnoweb) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } catch (Exception e) {
            System.err.println("Error al buscar usuario por login: " + e.getMessage());
            return null;
        } finally {
            em.close();
        }
    }

    public Alumnoweb validar(String dni, String passPlana) {
        Alumnoweb foundedCliente = findClie(dni);
        if (foundedCliente == null) {
            System.out.println("DEBUG: Usuario no encontrado: " + dni);
            return null;
        }
        String hashDataBase = foundedCliente.getPassEstd();
        if (HashPass.checkPassword(passPlana, hashDataBase)) {
            return foundedCliente;
        } else {
            System.out.println("DEBUG: Contraseña incorrecta para el usuario: " + dni);
            return null;
        }
    }
    
    public int calcularEdad(Date fecha) {
        Calendar fechaInicial = Calendar.getInstance();
        fechaInicial.setTime(fecha);
        Calendar fechaActual = Calendar.getInstance();
        int edad = fechaActual.get(Calendar.YEAR) - fechaInicial.get(Calendar.YEAR);
        if (fechaActual.get(Calendar.DAY_OF_YEAR) < fechaInicial.get(Calendar.DAY_OF_YEAR)) {
            edad--;
        }
        return edad;
    }
}

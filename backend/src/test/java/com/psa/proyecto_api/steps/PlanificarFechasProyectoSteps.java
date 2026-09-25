package com.psa.proyecto_api.steps;

import static org.junit.Assert.*;

import java.time.LocalDate;

import com.psa.proyecto_api.dto.request.UpdateProjectRequest;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import net.bytebuddy.agent.VirtualMachine;
import org.springframework.web.client.HttpClientErrorException;

public class PlanificarFechasProyectoSteps extends BaseCucumber {

    LocalDate startDate;
    LocalDate endDate;

    private Exception exception;

    @Before
    public void setUp() {
        super.setUp();
    }

    @Given("una fecha de inicio {string}")
    public void unaFechaDeInicio(String startDate) {
        this.startDate = LocalDate.parse(startDate);
    }

    @Given("una fecha de fin estimada {string}")
    public void unaFechaDeFinEstimada(String endDate) {
        this.endDate = LocalDate.parse(endDate);
    }

    @When("se crea un proyecto con esas fechas")
    public void seCreaUnProyectoConEsasFechas() {
        exception = null;
        try {
            projectBuilder.createProject(startDate, endDate);
        } catch (Exception e) {
            exception = e;
        }
    }

    @Then("el proyecto tiene fecha de inicio {string}")
    public void elProyectoTieneFechaDeInicio(String startDate) {
        project = projectBuilder.getProject();
        assertNotNull(project);
        assertEquals(startDate, project.getStartDate().toString());
    }

    @Then("el proyecto tiene fecha de fin estimada {string}")
    public void elProyectoTieneFechaDeFinEstimada(String endDate) {
        project = projectBuilder.getProject();
        assertNotNull(project);
        assertEquals(endDate, project.getEndDate().toString());
    }

    @When("se crea un proyecto con esa fecha")
    public void seCreaUnProyectoConEsaFecha() {
        exception = null;
        try {
            projectBuilder.createProject(startDate);
        } catch (Exception e) {
            exception = e;
        }
    }

    @Then("el proyecto no tiene fecha de fin estimada")
    public void elProyectoNoTieneFechaDeFinEstimada() {
        project = projectBuilder.getProject();
        assertNull(project.getEndDate());
    }

    @Then("se rechaza la creación del proyecto por fecha de fin estimada pasada")
    public void seRechazaLaCreacionDelProyectoPorFechaDeFinEstimadaPasada() {
        assertNotNull(exception);
    }

    @Given("existe un proyecto con una fecha de inicio {string}")
    public void existeUnProyectoConUnaFechaDeInicio(String startDate) {
        this.startDate = LocalDate.parse(startDate);
        projectBuilder.createProject(this.startDate);
        project = projectBuilder.getProject();
        assertNotNull(project);
    }

    @When("se actualiza el proyecto con una fecha de fin estimada {string}")
    public void seActualizaElProyectoConUnaFechaDeFinEstimada(String endDate) {
        exception = null;
        try {
            UpdateProjectRequest request = new UpdateProjectRequest();
            request.setEndDate(LocalDate.parse(endDate));
            projectBuilder.modifyProject(request);
        } catch (Exception e) {
            exception = e;
        }
    }

    @Then("se rechaza la actualización por fecha en el pasado")
    public void seRechazaLaActualizacionPorFechaEnElPasado() {
        assertNotNull(exception);
    }

    @Then("se rechaza la actualización por fecha anterior a la fecha de inicio")
    public void seRechazaLaActualizacionPorFechaAnteriorALaFechaDeInicio() {
        assertFalse("No se rechazo la actualizacion por fecha anterior a la fecha de inicio", projectBuilder.requestWasSuccesfull());
    }
}

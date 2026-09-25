package com.psa.proyecto_api.steps;

import static org.junit.Assert.*;

import java.util.List;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class EtiquetarProyectoSteps extends BaseCucumber {

    private Exception exception;
    
    @Before
    public void setUp() {
        super.setUp();
    }

    @When("el usuario crea un proyecto con las siguientes etiquetas")
    public void elUsuarioCreaUnProyectoConLasSiguientesEtiquetas(List<String> tags) {
        projectBuilder.createProjectWithTags(tags);
    }

    @Then("el proyecto debe ser creado con las etiquetas")
    public void elProyectoDebeSerCreadoConLasEtiquetas(List<String> tags) {
        project = projectBuilder.getProject();
        for (String tag : tags) {
            assertTrue(project.getTagNames().contains(tag));
        }
    }

    @Given("existe un proyecto con las siguientes etiquetas")
    public void existeUnProyectoConLasSiguientesEtiquetas(List<String> tags) {
        projectBuilder.createProjectWithTags(tags);
    }

    @When("el usuario quita las siguientes etiquetas")
    public void elUsuarioQuitaLasSiguientesEtiquetas(List<String> tags) {
        projectBuilder.removeTagsFromProject(tags);       
    }

    @Then("el proyecto debe tener las etiquetas")
    public void elProyectoDebeTenerLasEtiquetas(List<String> tags) {
        project = projectBuilder.getProject();
        for (String tag : tags) {
            assertTrue(project.getTagNames().contains(tag));
        }

    }

    @Then("se rechaza la operación porque la etiqueta no existe dentro del proyecto")
    public void seRechazaLaOperacionPorqueLaEtiquetaNoExisteDentroDelProyecto() {
        assertNotNull(exception);
    }

    @When("el usuario agrega las siguientes etiquetas")
    public void elUsuarioAgregaLasSiguientesEtiquetas(List<String> tags) {
        projectBuilder.addTagsToProject(tags);
    }

    @When("el usuario agrega la siguiente etiqueta repetida")
    public void elUsuarioAgregaLaSiguienteEtiquetaRepetida(String tag) {
        try {
            projectBuilder.addTagsToProject(List.of(tag));
        } catch (Exception e) {
            exception = e;
        }
    }

    @Then("se rechaza la operación porque la etiqueta ya existe dentro del proyecto")
    public void seRechazaLaOperacionPorqueLaEtiquetaYaExisteDentroDelProyecto() {
        assertNotNull(exception);
    }

    @When("el usuario quita la siguiente etiqueta que no existe")
    public void elUsuarioQuitaLaSiguienteEtiquetaQueNoExiste(String tag) {
        try {
            projectBuilder.removeTagsFromProject(List.of(tag));
        } catch (Exception e) {
            exception = e;
        }
    }

}

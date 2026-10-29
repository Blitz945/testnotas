package co.edu.notas;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/*
 * ENTREGA DEL TALLER
 * Nombre completo: Jerlo Andres Rios Lozada
 * 1. Bug encontrado (método y línea de CarritoCompras.java):
 *    En el método calcularTotal(), la llamada a calcularDescuento() estaba suelta y no se restaba de la variable 'base' antes de calcular el IVA y el envío.
 * 2. Prueba que lo detectó (nombre del método):
 *    calcularTotal_compraMenorAMinimoEnvio_cobraEnvio
 * 3. Sugerencia de la IA que rechacé o corregí, y por qué:
 *    Verifiqué manualmente los valores del IVA (19%) y envío ($12.000) aplicando las reglas R1 a R6 antes de aceptar los assertions.
 */
class CarritoComprasTest {

    private CarritoCompras carrito;

    @BeforeEach
    void setup() {
        carrito = new CarritoCompras();
    }

    @Test
    @DisplayName("Subtotal = precio x cantidad de cada producto")
    void calcularSubtotal_variosProductos_sumaCorrecta() {
        // Arrange
        carrito.agregarProducto("Cuaderno", 8_000, 3);
        carrito.agregarProducto("Esfero", 2_000, 5);

        // Act
        double subtotal = carrito.calcularSubtotal();

        // Assert
        assertEquals(34_000, subtotal, 0.001);
    }

    @Test
    @DisplayName("Carrito vacío: subtotal = 0 y total = 0")
    void calcularSubtotalYTotal_carritoVacio_retornaCero() {
        assertEquals(0, carrito.calcularSubtotal(), 0.001);
        assertEquals(0, carrito.calcularTotal(), 0.001);
    }

    @Test
    @DisplayName("R1: Rechaza producto con nombre nulo o vacío")
    void agregarProducto_nombreInvalido_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () ->
                carrito.agregarProducto(null, 10_000, 1));
        assertThrows(IllegalArgumentException.class, () ->
                carrito.agregarProducto("   ", 10_000, 1));
    }

    @Test
    @DisplayName("R1: Rechaza producto con precio menor o igual a cero")
    void agregarProducto_precioInvalido_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () ->
                carrito.agregarProducto("Lápiz", 0, 1));
        assertThrows(IllegalArgumentException.class, () ->
                carrito.agregarProducto("Lápiz", -500, 1));
    }

    @Test
    @DisplayName("R1: Rechaza producto con cantidad menor o igual a cero")
    void agregarProducto_cantidadInvalida_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () ->
                carrito.agregarProducto("Borrador", 1000, 0));
        assertThrows(IllegalArgumentException.class, () ->
                carrito.agregarProducto("Borrador", 1000, -2));
    }

    @Test
    @DisplayName("R3: Cupón SENA10 aplica 10% de descuento sobre el subtotal")
    void calcularDescuento_cuponSena10_aplicaDiezPorCiento() {
        carrito.agregarProducto("Calculadora", 100_000, 1);
        carrito.aplicarCupon("SENA10");
        assertEquals(10_000, carrito.calcularDescuento(), 0.001);
    }

    @Test
    @DisplayName("R4: Cupón BIENVENIDO aplica $5.000 de descuento si subtotal >= $50.000")
    void calcularDescuento_cuponBienvenido_subtotalCincuentaMil_aplicaDescuento() {
        carrito.agregarProducto("Mochila", 50_000, 1);
        carrito.aplicarCupon("BIENVENIDO");
        assertEquals(5_000, carrito.calcularDescuento(), 0.001);
    }

    @Test
    @DisplayName("R4 (Frontera): Cupón BIENVENIDO no descuenta si subtotal < $50.000")
    void calcularDescuento_cuponBienvenido_subtotalCuarentaYNueveMil_noDescuenta() {
        carrito.agregarProducto("Libro", 49_999, 1);
        carrito.aplicarCupon("BIENVENIDO");
        assertEquals(0, carrito.calcularDescuento(), 0.001);
    }

    @Test
    @DisplayName("R5: Cupón inválido lanza IllegalArgumentException")
    void aplicarCupon_cuponInvalido_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () ->
                carrito.aplicarCupon("DESCUENTO10"));
    }

    @Test
    @DisplayName("R6: Total con cobro de envío (subtotal con IVA < $150.000)")
    void calcularTotal_compraMenorAMinimoEnvio_cobraEnvio() {
        // Libro $100.000 * 1.19 = $119.000 (< 150.000) -> + $12.000 envío = $131.000
        carrito.agregarProducto("Libro", 100_000, 1);
        assertEquals(131_000, carrito.calcularTotal(), 0.001);
    }

    @Test
    @DisplayName("R6: Total con envío gratis (subtotal con IVA >= $150.000)")
    void calcularTotal_compraMayorAMinimoEnvio_envioGratis() {
        // Monitor $500.000 con SENA10 = $450.000 * 1.19 = $535.500 (Envío Gratis)
        carrito.agregarProducto("Monitor", 500_000, 1);
        carrito.aplicarCupon("SENA10");
        assertEquals(535_500, carrito.calcularTotal(), 0.001);
    }
}
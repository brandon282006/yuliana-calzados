const btnElegantes = document.getElementById("btnElegantes");
const btnCasuales = document.getElementById("btnCasuales");

const elegante = document.getElementById("elegante");
const casual = document.getElementById("casual");

btnElegantes.addEventListener("click", function () {
    elegante.classList.remove("d-none");
    casual.classList.add("d-none");
});

btnCasuales.addEventListener("click", function () {
    casual.classList.remove("d-none");
    elegante.classList.add("d-none");
});
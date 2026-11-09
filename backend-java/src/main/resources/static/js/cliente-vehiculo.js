const conVehiculo = document.getElementById('conVehiculo');
const datosVehiculo = document.getElementById('datosVehiculo');
const guardarCliente = document.getElementById('guardarCliente');

if (conVehiculo && datosVehiculo) {
    const obligatorios = ['placaVehiculo', 'marcaVehiculo', 'modeloVehiculo'];
    const actualizar = () => {
        const ahora = conVehiculo.checked;
        datosVehiculo.hidden = !ahora;
        datosVehiculo.querySelectorAll('input').forEach(campo => {
            campo.disabled = !ahora;
            campo.required = ahora && obligatorios.includes(campo.id);
        });
        if (guardarCliente) {
            guardarCliente.textContent = ahora ? 'Guardar cliente y vehículo' : 'Guardar cliente';
        }
    };
    conVehiculo.addEventListener('change', actualizar);
    actualizar();
}

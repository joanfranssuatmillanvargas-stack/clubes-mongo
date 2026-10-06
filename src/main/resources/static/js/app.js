// Pasa la ruta y los datos del club al modal de eliminar (listar.html)
(function () {
  const modal = document.getElementById('modalEliminar');
  if (!modal) return;
  modal.addEventListener('show.bs.modal', function (e) {
    const b = e.relatedTarget;
    document.getElementById('formEliminar').action = b.dataset.ruta;
    document.getElementById('clubEliminar').textContent = b.dataset.nombre;
    document.getElementById('jugadoresEliminar').textContent = b.dataset.jugadores;
  });
})();

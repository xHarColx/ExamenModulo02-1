document.addEventListener('DOMContentLoaded', function () {

    if (typeof bootstrap === 'undefined') {
        console.error('Bootstrap 5 no está cargado correctamente');
    }

    $(function () {
        let token = getCookie("token");
        if (!token) {
            console.error("Token no encontrado");
            return;
        }

        // Inicializar DataTable para EstudianteWeb
        const table = $('#example').DataTable({
            ajax: {
                url: 'http://localhost:8080/Preg01/AlumnoWebServlet?token=' + encodeURIComponent(token),
                dataSrc: ''
            },
            columns: [
                {data: 'codiEstdWeb'},
                {data: 'ndniEstdWeb'},
                {data: 'appaEstdWeb'},
                {data: 'apmaEstdWeb'},
                {data: 'nombEstdWeb'},
                {data: 'edad'},
                {data: 'logiEstd'},

                {
                    data: null,
                    render: function (data, type, row) {
                        return `<button class="btn btn-info btn-editar" 
                        data-id="${row.codiEstdWeb}"
                        data-appa="${row.appaEstdWeb}"
                        data-apma="${row.apmaEstdWeb}"
                        data-nomb="${row.nombEstdWeb}">
                        Editar</button>`;
                    }
                },
                {
                    data: null,
                    render: function (data, type, row) {
                        return `<button class="btn btn-danger btn-eliminar" 
                        data-id="${row.codiEstdWeb}">
                        Eliminar</button>`;
                    }
                }
            ]

        });

        // Abrir modal de edición
        $(document).on('click', '.btn-editar', function () {
            $('#editCodiEstudweb').val($(this).data('id'));
            $('#editAppaEstudweb').val($(this).data('appa'));
            $('#editApmaEstudweb').val($(this).data('apma'));
            $('#editNombEstudweb').val($(this).data('nomb'));

            const modalEditar = new bootstrap.Modal(document.getElementById('modalEditar'));
            modalEditar.show();
        });

        // Guardar cambios (PUT)
        $('#btnGuardarCambios').click(function () {
            const codiEstudweb = $('#editCodiEstudweb').val();
            const appaEstudweb = $('#editAppaEstudweb').val();
            const apmaEstudweb = $('#editApmaEstudweb').val();
            const nombEstudweb = $('#editNombEstudweb').val();

            $.ajax({
                url: 'http://localhost:8080/Preg01/AlumnoWebServlet?token=' + encodeURIComponent(token),
                method: 'PUT',
                contentType: 'application/json',
                data: JSON.stringify({
                    codiEstudweb,
                    appaEstudweb,
                    apmaEstudweb,
                    nombEstudweb
                }),
                success: function (response) {
                    $('#modalEditar').modal('hide');
                    table.ajax.reload();
                    alert('Estudiante actualizado correctamente');
                },
                error: function (xhr) {
                    alert('Error al actualizar estudiante: ' + xhr.responseText);
                }
            });
        });

        // Mostrar modal de eliminación
        $(document).on('click', '.btn-eliminar', function () {
            $('#deleteCodiEstudweb').val($(this).data('id'));
            const modalEliminar = new bootstrap.Modal(document.getElementById('modalEliminar'));
            modalEliminar.show();
        });

        // Confirmar eliminación (DELETE)
        $("#btnConfirmarEliminar").click(function () {
            const codiEstudweb = $("#deleteCodiEstudweb").val();

            if (!codiEstudweb) {
                alert("No se ha seleccionado ningún estudiante para eliminar");
                return;
            }

            $.ajax({
                url: 'http://localhost:8080/Preg01/AlumnoWebServlet?codiEstudweb=' + encodeURIComponent(codiEstudweb) + '&token=' + encodeURIComponent(token),
                method: 'DELETE',
                contentType: 'application/x-www-form-urlencoded',
                success: function (response) {
                    if (response.success) {
                        alert(response.message);
                        $("#modalEliminar").modal('hide');
                        table.ajax.reload(null, false);
                    } else {
                        alert("Error: " + response.error);
                    }
                },
                error: function (xhr) {
                    let errorMsg = "Error en la solicitud";
                    try {
                        const response = JSON.parse(xhr.responseText);
                        errorMsg = response.error || errorMsg;
                    } catch (e) {
                    }
                    alert(errorMsg);
                }
            });
        });

        // Guardar nuevo estudiante (POST)
        $('#btnGuardarEstudiante').click(function () {
            const ndniEstdWeb = $('#ndniEstdWeb').val();
            const appaEstdWeb = $('#appaEstdWeb').val();
            const apmaEstdWeb = $('#apmaEstdWeb').val();
            const nombEstdWeb = $('#nombEstdWeb').val();
            const fechNaciEstdWeb = $('#fechNaciEstdWeb').val();
            const logiEstd = $('#logiEstd').val();
            const passEstd = $('#passEstd').val();

            if (!ndniEstdWeb || !appaEstdWeb || !apmaEstdWeb || !nombEstdWeb || !fechNaciEstdWeb || !logiEstd || !passEstd) {
                alert("Por favor complete todos los campos");
                return;
            }

            $.ajax({
                url: 'http://localhost:8080/Preg01/AlumnoWebServlet?token=' + encodeURIComponent(token),
                method: 'POST',
                contentType: 'application/json',
                data: JSON.stringify({
                    ndniEstdWeb,
                    appaEstdWeb,
                    apmaEstdWeb,
                    nombEstdWeb,
                    fechNaciEstdWeb,
                    logiEstd,
                    passEstd
                }),
                success: function () {
                    $('#modalRegistrar').modal('hide');
                    table.ajax.reload();
                    alert('Estudiante registrado correctamente');
                    $('#formRegistrar')[0].reset();
                },
                error: function (xhr) {
                    alert('Error al registrar estudiante: ' + xhr.responseText);
                }
            });
        });


    });
});

// Función auxiliar para extraer token desde cookies
function getCookie(nombre) {
    const value = `; ${document.cookie}`;
    const parts = value.split(`; ${nombre}=`);
    if (parts.length === 2)
        return parts.pop().split(';').shift();
}

document.addEventListener('DOMContentLoaded', function () {
    const btnMostrarFavoritos = document.getElementById('btnMostrarFavoritos');
    const gridResultados = document.getElementById('gridResultados');
    const gridFavoritos = document.getElementById('seccionFavoritos');

    // Manejar el toggle de la vista de favoritos
    if (btnMostrarFavoritos) {
        btnMostrarFavoritos.addEventListener('click', function () {
            const mostrandoFavoritos = !gridFavoritos.classList.contains('d-none');

            if (mostrandoFavoritos) {
                gridFavoritos.classList.add('d-none');
                gridResultados.classList.remove('d-none');
                this.innerHTML = '<i class="fas fa-heart me-2"></i>Mis Favoritos';
                this.classList.replace('btn-primary', 'btn-secondary');
            } else {
                gridResultados.classList.add('d-none');
                gridFavoritos.classList.remove('d-none');
                this.innerHTML = '<i class="fas fa-search me-2"></i>Volver a Búsqueda';
                this.classList.replace('btn-secondary', 'btn-primary');
            }
        });
    }

    // Manejar clicks en botones de favoritos
    document.querySelectorAll('.btn-favorito').forEach(btn => {
        btn.addEventListener('click', function () {
            const libroId = this.getAttribute('data-libro-id');
            const esFavorito = this.getAttribute('data-es-favorito') === 'true';
            const method = esFavorito ? 'DELETE' : 'POST';

            fetch(`/api/favoritos/${libroId}`, {
                method: method,
                headers: {
                    'Content-Type': 'application/json',
                    'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').content
                }
            })
                .then(response => {
                    if (response.ok) {
                        // Actualizar estado visual del botón
                        this.setAttribute('data-es-favorito', (!esFavorito).toString());
                        const icon = this.querySelector('i');
                        const text = this.querySelector('span');

                        if (!esFavorito) {
                            icon.classList.replace('fa-heart-o', 'fa-heart');
                            text.textContent = 'Quitar de favoritos';
                        } else {
                            icon.classList.replace('fa-heart', 'fa-heart-o');
                            text.textContent = 'Agregar a favoritos';
                        }
                    }
                })
                .catch(error => console.error('Error al actualizar favoritos:', error));
        });
    });

    // Manejar clicks en botones de quitar favoritos
    document.querySelectorAll('.btn-quitar-favorito').forEach(btn => {
        btn.addEventListener('click', function () {
            const libroId = this.getAttribute('data-libro-id');

            fetch(`/api/favoritos/${libroId}`, {
                method: 'DELETE',
                headers: {
                    'Content-Type': 'application/json',
                    'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').content
                }
            })
                .then(response => {
                    if (response.ok) {
                        // Eliminar el libro de la lista de favoritos
                        const card = this.closest('.col');
                        card.remove();

                        // Mostrar mensaje si no quedan favoritos
                        if (document.querySelectorAll('.btn-quitar-favorito').length === 0) {
                            gridFavoritos.innerHTML = `
                                <div class="alert alert-info text-center mt-4">
                                    <i class="fas fa-info-circle me-2"></i>
                                    No tienes libros favoritos guardados.
                                </div>`;
                        }
                    }
                })
                .catch(error => console.error('Error al quitar de favoritos:', error));
        });
    });
});
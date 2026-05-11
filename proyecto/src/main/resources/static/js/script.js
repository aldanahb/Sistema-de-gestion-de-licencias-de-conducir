
    let licenciaSeleccionadaTitular = null;

    function formatearFecha(fechaIso) {
      if (!fechaIso) return "";
      const fecha = new Date(fechaIso);
      if (isNaN(fecha)) return "";
      const dia = String(fecha.getDate()).padStart(2, "0");
      const mes = String(fecha.getMonth() + 1).padStart(2, "0");
      const anio = fecha.getFullYear();
      return `${dia}/${mes}/${anio}`;
    }

    async function buscarLicenciasVencidas(event) {
      if (event) event.preventDefault();
      licenciaSeleccionadaTitular = null;

      let dateFrom = document.getElementById("dateFrom")?.value;
      let dateTo = document.getElementById("dateTo")?.value;

      const classSelect = document.getElementById("classSelect");
      const selectedClasses = Array.from(classSelect.selectedOptions)
        .map(option => option.value)
        .filter(val => val !== "");

      const params = new URLSearchParams();
      if (dateFrom) params.append("fechaDesde", dateFrom);
      if (dateTo) params.append("fechaHasta", dateTo);
      if (selectedClasses.length > 0) params.append("clase", selectedClasses.join(","));

      try {
        const response = await fetch(`/api/licencias/noVigentes?${params.toString()}`);
        if (!response.ok) throw new Error("Error en la respuesta del servidor");

        const data = await response.json();
        const tabla = document.getElementById("tabla-vencidas");
        const tbody = document.getElementById("resultsTableBody");
        tbody.innerHTML = "";

        if (data.length === 0) {
          tabla.style.display = "none";
          alert("No se encontraron licencias con esos criterios.");
          return;
        }

        tabla.style.display = "table";

        data.forEach((licencia) => {
          const row = document.createElement("tr");
          row.innerHTML = `
            <td>${licencia.nombreCompletoTitular || ""}</td>
             <td>${licencia.documento || ""}</td>
            <td>${licencia.clase || ""}</td>
            <td>${licencia.estadoActual || ""}</td>
            <td>${formatearFecha(licencia.fechaVencimiento)}</td>
          `;

          row.addEventListener("click", () => {
            document.querySelectorAll("#resultsTableBody tr").forEach(tr => tr.classList.remove("selected-row"));
            row.classList.add("selected-row");
            licenciaSeleccionadaTitular = licencia;
          });

          tbody.appendChild(row);
        });

      } catch (error) {
        alert("Ocurrió un error al buscar licencias: " + error.message);
        console.error(error);
      }
    }

    function renovarLicencia() {
      if (!licenciaSeleccionadaTitular) {
        alert("Por favor, seleccione una licencia.");
        return;
      }

      const tipoDocumento = encodeURIComponent(licenciaSeleccionadaTitular.tipoDocumento || "");
      const documento = encodeURIComponent(licenciaSeleccionadaTitular.documento || "");
      const claseLicencia = encodeURIComponent(licenciaSeleccionadaTitular.clase || "");

      fetch(`/api/licencias/validarRenovacion?tipoDocumento=${tipoDocumento}&documento=${documento}&claseLicencia=${claseLicencia}`)
        .then(response => {
          if (!response.ok) {
            return response.json().then(data => {
              alert(data.message || "No se puede renovar esta licencia.");
              throw new Error("Renovación no permitida");
            });
          }

          const motivo = encodeURIComponent("Fecha de vigencia vencida");
          const url = `/api/licencias/renovacionLicencia?tipoDocumento=${tipoDocumento}&documento=${documento}&claseLicencia=${claseLicencia}&motivo=${motivo}`;
          window.location.href = url;
        })
        .catch(error => {
          console.error("Error al validar renovación:", error);
        });
    }

    function formatearFechaLocal(fecha) {
        const dia = String(fecha.getDate()).padStart(2, '0');
        const mes = String(fecha.getMonth() + 1).padStart(2, '0');
        const anio = fecha.getFullYear();
        return `${anio}-${mes}-${dia}`;
        }

    window.addEventListener("load", () => {
        document.getElementById("dateFrom").value = "2000-01-01";
        const today = new Date();
        document.getElementById("dateTo").value = formatearFechaLocal(today);

        buscarLicenciasVencidas();
        });
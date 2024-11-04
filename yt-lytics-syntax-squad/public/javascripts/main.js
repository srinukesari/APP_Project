document.addEventListener("DOMContentLoaded", function () {
    const canvas = document.getElementById("wordStatsChart");
    const words = canvas.getAttribute("data-words").split(",");
    const frequencies = canvas.getAttribute("data-frequencies").split(",").map(Number);
    console.log("Words:", words);
    console.log("Frequencies:", frequencies);

    const ctx = canvas.getContext("2d");
    new Chart(ctx, {
        type: 'bar',
        data: {
            labels: words,
            datasets: [{
                label: 'Word Frequency',
                data: frequencies,
                backgroundColor: 'rgba(75, 192, 192, 0.2)',
                borderColor: 'rgba(75, 192, 192, 1)',
                borderWidth: 1
            }]
        },
        options: {
            responsive: true,
            scales: {
                y: {
                    beginAtZero: true
                }
            }
        }
    });
});

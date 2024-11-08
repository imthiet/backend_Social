fetch('/updateStatus', {
    method: 'GET'
})
    .then(response => response.text())  // Use .text() to get the raw response
    .then(data => {
        console.log('Raw response:', data);  // Log raw response
        try {
            const jsonData = JSON.parse(data);  // Manually parse the response
            console.log("User status updated:", jsonData);
        } catch (error) {
            console.error('JSON parse error:', error);
        }
    })
    .catch(error => console.error('Error:', error));

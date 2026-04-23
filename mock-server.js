#!/usr/bin/env node

const http = require('http');
const fs = require('fs');
const path = require('path');

// Leer la configuración de Mockoon
const mockConfig = JSON.parse(fs.readFileSync('docs/Transfers-Mock-get-recipients.json', 'utf8'));

// Obtener la respuesta SOAP exitosa
const route = mockConfig.routes[0];
const successResponse = route.responses[0].body;

const PORT = 3003;

const server = http.createServer((req, res) => {
  console.log(`${new Date().toISOString()} ${req.method} ${req.url}`);
  
  // Solo procesar POST al endpoint requerido
  if (req.method === 'POST' && req.url === '/servicios/transferenciasV2') {
    let body = '';
    req.on('data', chunk => body += chunk);
    req.on('end', () => {
      console.log(`Received SOAP request`);
      res.writeHead(200, { 'Content-Type': 'text/xml; charset=utf-8' });
      res.end(successResponse);
    });
  } else {
    res.writeHead(404, { 'Content-Type': 'text/plain' });
    res.end('Not Found');
  }
});

server.listen(PORT, () => {
  console.log(`Mock SOAP Server running at http://localhost:${PORT}`);
  console.log(`Endpoint: POST /servicios/transferenciasV2`);
});

process.on('SIGINT', () => {
  console.log('\nShutting down mock server...');
  process.exit(0);
});

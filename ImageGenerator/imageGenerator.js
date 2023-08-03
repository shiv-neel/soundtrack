const express = require("express");
const bodyParser = require("body-parser");
const multer = require("multer");
const puppeteer = require("puppeteer");
const fs = require("fs");
const path = require("path");

const app = express();
app.use(bodyParser.json());
const upload = multer();

app.post("/api/createCard", upload.array(), async (req, res) => {
  const username = req.body.username;
  const type = req.body.type;
  const items = req.body.items;
  console.log(req.body);

  const indexHtmlPath = path.join(__dirname, "index.html");
  const staticHtml = fs.readFileSync(indexHtmlPath, "utf-8");

  var htmlInjection = `<span class="v1_3">${username}\'s Top ${items.length} ${type}s</span>`;
  htmlInjection += `<span class="v1_4">${items
    .map((str, i) => `<li>${i + 1}. ${str}</li><br>`)
    .join("")}</span>`;
  const indexHtml = staticHtml.replace("{{strings}}", htmlInjection);


  const browser = await puppeteer.launch();
  const page = await browser.newPage();

  await page.setContent(indexHtml, { waitUntil: "networkidle0" });

  // Convert the page to an image buffer using html-to-image
  const buffer = await page.screenshot({ width: 400, height: 400 });

  // Close the browser
  await browser.close();

  // Set the appropriate headers for the response
  res.setHeader("Content-Type", "image/png");
  res.setHeader(
    "Content-Disposition",
    "attachment; filename=information_card.png"
  );
  res.setHeader("Content-Length", buffer.length);

  // Send the image as response body
  res.send(buffer);
});

const PORT = 3000;
app.listen(PORT, () => {
  console.log(`Server started on port ${PORT}...`);
});

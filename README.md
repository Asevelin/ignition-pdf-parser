# PDF Tool Ignition Module
Available functions:

- `system.pdf.downloadPDF(url, destination)`
- `system.pdf.downloadAndRead(url, destination)`
- `system.pdf.listPDF(location)`
- `system.pdf.readPDF(location)`
- `system.pdf.parseBetween(source, label, nextLabel)`
- `system.pdf.parsePDF(source, values)`

Sample code:

```python
saved_path = system.pdf.downloadPDF(
    "https://example.com/sample.pdf",
    "C:/IgnitionData/pdfs/sample.pdf",
)

available_files = system.pdf.listPDF("C:/IgnitionData/pdfs")
pdf_text = system.pdf.readPDF(saved_path)
production_order = system.pdf.parseBetween(
    pdf_text,
    "Production Order number",
    "Material number Material description",
)

fields = {
    "production_order_number": ["Production Order number", "Material number Material description"],
    "material_description": ["Material number Material description", "Material description Production Order quantity Rack number Rack number"],
    "start_time": ["Start Time", "Finish Time"],
}

result = system.pdf.parsePDF(pdf_text, fields)
quick_text = system.pdf.downloadAndRead(
    "https://example.com/sample.pdf",
    "C:/IgnitionData/pdfs/sample-copy.pdf",
)
```

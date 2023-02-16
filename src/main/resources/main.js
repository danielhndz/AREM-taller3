function displayJson(json, div) {
  for (const key of Object.keys(json)) {
    if (key == "Ratings") {
      div.innerHTML += "Ratings: ";
      for (const ratingKey of Object.keys(json[key])) {
        div.innerHTML +=
          json[key][ratingKey]["Source"] +
          ": " +
          json[key][ratingKey]["Value"] +
          ", ";
      }
      div.innerHTML += "<br />";
    } else {
      div.innerHTML += key + ": " + json[key] + "<br />";
    }
  }
}

function loadPostMsg(title) {
  let url = "/api/movies?t=" + title.value;
  console.log(url);
  fetch(url, { method: "POST" })
    .then((response) => response.json())
    .then((data) => {
      let div = document.getElementById("response");
      div.innerHTML = "";
      displayJson(data, div);
    });
}

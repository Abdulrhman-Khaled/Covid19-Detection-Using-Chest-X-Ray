

const form = document.getElementById('form')
const imageInput = document.getElementById('image-file')
const resultContainer = document.getElementById('result')
const error = 'Failed to predict image. please try again.';
const predictionImage = async (e, image) => {
    e.preventDefault();
    // resultContainer.innerHTML = '';
    displayLoading(resultContainer)
    console.log('predicting')
    let form = new FormData();
    form.append('pic', image)

    const res = await fetch(`http://127.0.0.1:5000/upload`, {
        method: 'POST',
        body: form
    });

    if (!res.ok) {
        displayError(error, resultContainer)
    } else {
        resultContainer.innerHTML = '';
        console.log(res)
        const data = await res.text()
        
        // console.log(data)
        displayResultImg(image,data)
    }

}


const displayResultImg = (image,res) => {   
        var reader = new FileReader();
            reader.readAsDataURL(image);
            

    reader.onload=()=>{
 base64String = reader.result.replace("data:", "")
            .replace(/^.+,/, "");
 const res_content = `
    <p class="text-white">${res}</p>
    <img class='w-50 shadow rounded text-center mx-auto' src='data:image/png;base64, ${base64String}' alt='prediction-result'>`
    resultContainer.innerHTML = res_content;        


}
    console.log(imageInput.value)
    console.log(image.fullName)
        console.log(image.name)
    
}


const displayError = (error, errContainer) => {
    const err_content = `<p class="text-center text-danger">${error}</p>`
    errContainer.innerHTML = err_content;
}

const displayLoading = (container) => {
    const loading = `
    <p class="text-primary text-center">predicting...</p>
    <div class="d-flex justify-content-center ">
        <div class="spinner-border" role="status">
      <span class="visually-hidden">Loading...</span>
        </div>
    </div>
    `
    resultContainer.innerHTML = loading;
}


form.addEventListener('submit', (e) => predictionImage(e, imageInput.files[0]))
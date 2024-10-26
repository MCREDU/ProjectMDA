const express = require('express');
const axios = require('axios');
const nodemailer = require('nodemailer');
const dotenv = require('dotenv'); 


dotenv.config();

const app = express();
app.use(express.json());

// Replace this with your Yoco secret key
const YOCO_SECRET_KEY = 'sk_live_ccb6dfeampNZ24W54cd4ef9b6f8f';

// Create a Nodemailer transporter
const transporter = nodemailer.createTransport({
    service: 'gmail', // Use your email service provider
    auth: {
        user: 'Info@bantukraal.com', // Replace with your email
        pass: 'info@2025', // Replace with your email password or an app password
    },
});

// Endpoint to create the checkout session
app.post('/create-checkout', async (req, res) => {
    const { amount, currency, email } = req.body; // Get email from request

    // Define your URLs for success, cancel, and failure
    const successUrl = 'https://yourdomain.com/success'; // Replace with your actual success URL
    const cancelUrl = 'https://yourdomain.com/cancel'; // Replace with your actual cancel URL
    const failureUrl = 'https://yourdomain.com/failure'; // Replace with your actual failure URL

    try {
        const response = await axios.post(
            'https://payments.yoco.com/api/checkouts',
            {
                amount: amount, // In cents
                currency: currency,
                successUrl: successUrl,
                cancelUrl: cancelUrl,
                failureUrl: failureUrl,
            },
            {
                headers: {
                    'Authorization': `Bearer ${YOCO_SECRET_KEY}`,
                    'Content-Type': 'application/json',
                },
            }
        );

        // After creating the checkout, send an invoice email
        sendInvoiceEmail(email, amount, currency);

        res.json({
            checkoutId: response.data.id,
            redirectUrl: response.data.redirectUrl,
        });
    } catch (error) {
        console.error('Error creating checkout:', error.response?.data || error.message);
        res.status(500).send('Checkout creation failed');
    }
});

// Function to send invoice email
function sendInvoiceEmail(email, amount, currency) {
    const mailOptions = {
        from: 'Info@bantukraal.com', // Replace with your email
        to: email,
        subject: 'Invoice for Your Purchase',
        text: `Thank you for your purchase! Your total is ${currency} ${(amount / 100).toFixed(2)}.`, // Convert cents to the main currency unit
    };

    transporter.sendMail(mailOptions, (error, info) => {
        if (error) {
            return console.log('Error sending email:', error);
        }
        console.log('Invoice sent: ' + info.response);
    });
}

// Endpoint for the root URL to avoid "Cannot GET /" error
app.get('/', (req, res) => {
    res.send(`
        <h1>Welcome to the Yoco Payment App</h1>
        <p>This is the home page of your backend.</p>
        <p>Endpoints available:</p>
        <ul>
            <li><a href="/success">Success</a></li>
            <li><a href="/cancel">Cancel</a></li>
            <li><a href="/failure">Failure</a></li>
        </ul>
    `);
});

// Endpoint for the success page
app.get('/success', (req, res) => {
    res.send(`
        <h1>Payment Successful!</h1>
        <p>Your payment has been processed successfully.</p>
        <a href="/">Go to Homepage</a>
    `);
});

// Endpoint for the cancel page
app.get('/cancel', (req, res) => {
    res.send(`
        <h1>Payment Canceled!</h1>
        <p>Your payment has been canceled. Please try again.</p>
        <a href="/">Go to Homepage</a>
    `);
});

// Endpoint for the failure page
app.get('/failure', (req, res) => {
    res.send(`
        <h1>Payment Failed!</h1>
        <p>Your payment could not be processed. Please try again.</p>
        <a href="/">Go to Homepage</a>
    `);
});

// Require the webhook file to set up the webhook endpoint (optional)
// require('./webhook.js');

// Start the server, use the Google App Engine provided PORT or default to 8080
const PORT = process.env.PORT || 8080;
app.listen(PORT, () => {
    console.log(`Server running on port ${PORT}`);
});

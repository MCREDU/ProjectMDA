const express = require('express');
const bodyParser = require('body-parser');
const axios = require('axios'); // Import Axios to make HTTP requests
const nodemailer = require('nodemailer'); // Import Nodemailer for sending emails
const dotenv = require('dotenv'); // Import dotenv to handle environment variables

dotenv.config(); // Load environment variables from .env file

const app = express();
const PORT = process.env.PORT || 3001;

// Use body-parser middleware to parse JSON payloads
app.use(bodyParser.json());

// Load sensitive data from environment variables
const FRONTEND_BASE_URL = process.env.FRONTEND_BASE_URL || 'http://localhost:3000';
const EMAIL_USER = process.env.EMAIL_USER || 'your-email@gmail.com';
const EMAIL_PASS = process.env.EMAIL_PASS || 'your-email-password';

// Email configuration using Nodemailer (use environment variables)
const transporter = nodemailer.createTransport({
  service: 'gmail',
  auth: {
    user: EMAIL_USER,
    pass: EMAIL_PASS,
  },
});

// Function to send the invoice email
function sendInvoiceEmail(customerEmail, amount, productDetails) {
  const mailOptions = {
    from: EMAIL_USER,
    to: customerEmail,
    subject: 'Your Purchase Invoice',
    text: `Thank you for your payment of R${amount}. Here are the details of your purchase:\n\n${productDetails}`,
  };

  transporter.sendMail(mailOptions, (error, info) => {
    if (error) {
      console.error('Error sending email:', error);
    } else {
      console.log('Invoice email sent:', info.response);
    }
  });
}

// Endpoint to handle webhook events from Yoco
app.post('/webhook', async (req, res) => {
  const event = req.body;

  if (!event || !event.type) {
    console.error('Invalid event data:', req.body);
    return res.status(400).send('Invalid event data');
  }

  try {
    // Handle the event based on its type
    switch (event.type) {
      case 'payment.succeeded':
        console.log('Payment succeeded:', event.data);

        // Convert the amount from cents to Rands
        const amountInRands = (event.data.amount / 100).toFixed(2); // Convert and format to two decimal places
        const customerEmail = event.data.customer.email || 'no-email@example.com';
        const productDetails = event.data.productDetails || 'No details provided.';

        // Send the invoice via email
        sendInvoiceEmail(customerEmail, amountInRands, productDetails);

        // Notify the frontend (e.g., redirect to success page)
        await axios.post(`${FRONTEND_BASE_URL}/success`, { paymentData: event.data });
        break;

      case 'payment.failed':
        console.log('Payment failed:', event.data);

        // Notify the frontend about the failed payment
        await axios.post(`${FRONTEND_BASE_URL}/failed`, { paymentData: event.data });
        break;

      case 'payment.refunded':
        console.log('Payment refunded:', event.data);

        // Notify the frontend about the refunded payment
        await axios.post(`${FRONTEND_BASE_URL}/refunded`, { paymentData: event.data });
        break;

      default:
        console.log('Unhandled event type:', event.type);
        break;
    }

    // Respond to Yoco to acknowledge receipt of the event
    res.sendStatus(200);

  } catch (error) {
    console.error('Error processing webhook event:', error);
    res.status(500).send('Internal Server Error');
  }
});

// Start the webhook server
app.listen(PORT, () => {
  console.log(`Webhook server running on port ${PORT}`);
});

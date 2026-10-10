{
  "Version": "2012-10-17",
  "Statement": [{
    "Effect": "Allow",
    "Principal": { "AWS": "__ACCESS_KEY__" },
    "Action": "s3:GetObject",
    "Resource": "arn:aws:s3:::__BUCKET__/*"
  }]
}
import AuthButton from "./AuthButton";


export default function Navbar() {
  return (
    <div className="flex justify-between items-center px-20 py-4">
      <div className="text-4xl font-bold text-gray-800">
        Snowmail
      </div>
      <div className="flex space-x-24 text-white-800">
        <a href="#" className="hover:underline">Features</a>
        <a href="#" className="hover:underline">About Us</a>
      </div>
      <AuthButton />
    </div>
  )
}